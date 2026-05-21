package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.response.OrderDailyStatResponse;
import io.github.ptus04.server.dto.response.RevenueDailyStatResponse;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.repository.TransactionRepository;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

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
    public ResponseEntity<List<OrderDailyStatResponse>> getOrderStats(
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

        List<OrderDailyStatResponse> result = new ArrayList<>();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            String date = String.format("%02d", day);
            result.add(new OrderDailyStatResponse(
                    date,
                    statsMap.getOrDefault(date, 0L)
            ));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue-stats")
    public ResponseEntity<List<RevenueDailyStatResponse>> getRevenueStats(
            @RequestParam int month,
            @RequestParam int year
    ) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        Map<String, BigDecimal> statsMap = transactionRepository
                .sumRevenueByDayInMonth(startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));

        List<RevenueDailyStatResponse> result = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            String date = String.format("%02d", day);
            result.add(new RevenueDailyStatResponse(
                    date,
                    statsMap.getOrDefault(date, BigDecimal.ZERO)
            ));
        }
        return ResponseEntity.ok(result);
    }
}