package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.model.ZoneRepository;
import org.knzoon.painthelper.representation.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final ZoneRepository zoneRepository;
    private final AtomicLong counter = new AtomicLong();
    private final List<Greeting> allGreetings = new ArrayList<>();

    @Autowired
    public GreetingController(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/greeting")
    public List<Greeting> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
//        try {
//            long valueOfThisRun = counter.incrementAndGet();
//            if (valueOfThisRun % 2 == 0) {
//                throw new ValidationException("Inga jämna tal tillåtna");
//            }
//            Greeting greeting = new Greeting(valueOfThisRun, String.format(template, name));
//            allGreetings.add(greeting);
//            return allGreetings;
//        } catch (ValidationException ex) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validering misslyckades", ex);
//        }
        Set<String> zonenames = Arrays.asList("Trollhyveln", name).stream().collect(Collectors.toSet());
        List<Zone> zones = zoneRepository.findByNameIn(zonenames);
        allGreetings.addAll(zones.stream().map(zone -> new Greeting(zone.getId(), zone.getName())).collect(Collectors.toList()));
        return allGreetings;
    }
}
