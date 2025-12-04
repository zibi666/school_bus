package com.lm.school_bus.service;

import com.lm.school_bus.repository.BusScheduleRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PlateNumberService {

    private static final String[] CITY_PREFIXES = {"京", "沪", "粤", "浙", "苏", "鲁", "湘"};
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final BusScheduleRepository busScheduleRepository;

    public PlateNumberService(BusScheduleRepository busScheduleRepository) {
        this.busScheduleRepository = busScheduleRepository;
    }

    public String generatePlateNumber() {
        String plate = randomPlate();
        int attempts = 0;
        while (busScheduleRepository.existsById(plate) && attempts < 10_000) {
            plate = randomPlate();
            attempts++;
        }
        return plate;
    }

    private @org.springframework.lang.NonNull String randomPlate() {
        String city = CITY_PREFIXES[RANDOM.nextInt(CITY_PREFIXES.length)];
        char letter = LETTERS.charAt(RANDOM.nextInt(LETTERS.length()));
        int number = RANDOM.nextInt(90000) + 10000;
        return city + letter + number;
    }
}
