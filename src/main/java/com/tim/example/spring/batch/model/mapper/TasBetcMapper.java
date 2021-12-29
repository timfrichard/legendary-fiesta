package com.tim.example.spring.batch.model.mapper;

import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.TasBetc;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TasBetcMapper {

    TasBetcMapper tasBetcMapper = Mappers.getMapper(TasBetcMapper.class);

    TasBetc dtoTasBetcToTasBetc(TasBetcDTO tasBetcDTO);
}
