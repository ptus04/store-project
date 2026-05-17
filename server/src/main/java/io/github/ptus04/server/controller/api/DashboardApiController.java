package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.response.DailyOrderStatResponse;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final UserService userService;
    private final OrderRepository orderRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        long totalCustomers = userService.countByRole(UserRoleEnum.CUSTOMER);
        long totalEmployees = userService.countByRole(UserRoleEnum.EMPLOYEE);

        return ResponseEntity.ok(Map.of(
                "totalCustomers", totalCustomers,
                "totalEmployees", totalEmployees
        ));
    }

    @GetMapping("/order-stats")
    public ResponseEntity<List<DailyOrderStatResponse>> getOrderStats(
            @RequestParam int month,
            @RequestParam int year
    ) {
        ZoneId zoneVN = ZoneId.of("Asia/Ho_Chi_Minh");
        YearMonth yearMonth = YearMonth.of(year, month);

        Instant startDate = yearMonth.atDay(1)
                .atStartOfDay(zoneVN)
                .toInstant();

        Instant endDate = yearMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay(zoneVN)
                .toInstant();

        Map<String, Long> statsMap = orderRepository
                .countOrdersByDayInMonth(startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).longValue()
                ));

        List<DailyOrderStatResponse> result = new ArrayList<>();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            String date = String.format("%02d", day);
            result.add(new DailyOrderStatResponse(
                    date,
                    statsMap.getOrDefault(date, 0L)
            ));
        }

        return ResponseEntity.ok(result);
    }
}