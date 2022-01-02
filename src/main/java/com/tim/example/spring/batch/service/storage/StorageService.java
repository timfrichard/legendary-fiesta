package com.tim.example.spring.batch.service.storage;

import com.tim.example.spring.batch.properties.FileUploadJobProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class StorageService {

	private final FileUploadJobProperties fileUploadJobProperties;

	@Autowired
	public StorageService(final FileUploadJobProperties fileUploadJobProperties) {
		this.fileUploadJobProperties = fileUploadJobProperties;
	}

	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = fileUploadJobProperties.getFileUploadRootDirectory().resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(fileUploadJobProperties
					.getFileUploadRootDirectory().toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	public Stream<Path> loadAll() {
		try {
			return Files.walk(fileUploadJobProperties.getFileUploadRootDirectory(), 1)
				.filter(path -> !path.equals(fileUploadJobProperties.getFileUploadRootDirectory()))
				.map(fileUploadJobProperties.getFileUploadRootDirectory()::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	public Path load(String filename) {
		return fileUploadJobProperties.getFileUploadRootDirectory().resolve(filename);
	}

	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(fileUploadJobProperties.getFileUploadRootDirectory().toFile());
	}

	public void init() {
		try {
			Files.createDirectories(fileUploadJobProperties.getFileUploadRootDirectory());
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
