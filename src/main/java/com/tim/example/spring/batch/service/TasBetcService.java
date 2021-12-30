package com.tim.example.spring.batch.service;

import com.google.common.collect.Lists;
import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.repository.TasBetcRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TasBetcService {

    public static final String UNPRINTABLE_REGEX = "\\P{Print}";

    private final TasBetcRepository tasBetcRepository;

    public TasBetcService(TasBetcRepository tasBetcRepository) {
        this.tasBetcRepository = tasBetcRepository;
    }

    /**
     * Demo on stripping the unprintable characters from string characters.
     *
     * @param tasBetcDTO
     */
    public TasBetcDTO removeUnprintableCharacters(final TasBetcDTO tasBetcDTO) {

        tasBetcDTO.setAdminBureau(tasBetcDTO.getAdminBureau().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setBetc(tasBetcDTO.getBetc().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasA(tasBetcDTO.getComponentTasA().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasATA(tasBetcDTO.getComponentTasATA().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasAID(tasBetcDTO.getComponentTasAID().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasBPOA(tasBetcDTO.getComponentTasBPOA().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasSP(tasBetcDTO.getComponentTasSP().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasEPOA(tasBetcDTO.getComponentTasEPOA().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasMain(tasBetcDTO.getComponentTasMain().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setComponentTasSub(tasBetcDTO.getComponentTasSub().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setGwaTas(tasBetcDTO.getGwaTas().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));
        tasBetcDTO.setGwaTasName(tasBetcDTO.getGwaTasName().replaceAll(UNPRINTABLE_REGEX, StringUtils.EMPTY));

        return tasBetcDTO;
    }

    /**
     * Demo on stripping the leading and end whitespace from string characters.
     *
     * @param tasBetcDTO
     */
    public TasBetcDTO removeWhiteSpaces(final TasBetcDTO tasBetcDTO) {

        tasBetcDTO.setAdminBureau(StringUtils.stripToNull(tasBetcDTO.getAdminBureau()));
        tasBetcDTO.setBetc(StringUtils.stripToNull(tasBetcDTO.getBetc()));
        tasBetcDTO.setComponentTasA(StringUtils.stripToNull(tasBetcDTO.getComponentTasA()));
        tasBetcDTO.setComponentTasATA(StringUtils.stripToNull(tasBetcDTO.getComponentTasATA()));
        tasBetcDTO.setComponentTasAID(StringUtils.stripToNull(tasBetcDTO.getComponentTasAID()));
        tasBetcDTO.setComponentTasBPOA(StringUtils.stripToNull(tasBetcDTO.getComponentTasBPOA()));
        tasBetcDTO.setComponentTasSP(StringUtils.stripToNull(tasBetcDTO.getComponentTasSP()));
        tasBetcDTO.setComponentTasEPOA(StringUtils.stripToNull(tasBetcDTO.getComponentTasEPOA()));
        tasBetcDTO.setComponentTasMain(StringUtils.stripToNull(tasBetcDTO.getComponentTasMain()));
        tasBetcDTO.setComponentTasSub(StringUtils.stripToNull(tasBetcDTO.getComponentTasSub()));
        tasBetcDTO.setGwaTas(StringUtils.stripToNull(tasBetcDTO.getGwaTas()));

        return tasBetcDTO;
    }

    public TasBetc save(final TasBetc tasBetc) {
        return tasBetcRepository.save(tasBetc);
    }

    public List<TasBetc> saveAll(final List<? extends TasBetc> tasBetcs) {
        return Lists.newArrayList(tasBetcRepository.saveAll(tasBetcs));
    }
}
