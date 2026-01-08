package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.uasz.SIGTMA.service.DashboardStatsService;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin("*")
public class StatsController {

    @Autowired
    private DashboardStatsService statsService;

    @GetMapping
    public Map<String, Long> getDashboardStats() {
        return statsService.getStats();
    }
}
