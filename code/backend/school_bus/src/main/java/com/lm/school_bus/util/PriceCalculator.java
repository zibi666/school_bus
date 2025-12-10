package com.lm.school_bus.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 价格计算工具类
 * 根据开始和结束时间计算用车时长和价格
 */
public class PriceCalculator {

    /**
     * 根据开始和结束时间计算小时数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 小时数（包含小数）
     */
    public static double calculateHours(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始和结束时间不能为空");
        }

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }

        // 计算分钟数
        long durationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("使用时长必须大于0");
        }

        // 转换为小时（保留小数）
        return durationMinutes / 60.0;
    }

    /**
     * 计算订单价格
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param hourlyPrice 每小时价格
     * @return 总价格
     */
    public static BigDecimal calculatePrice(LocalDateTime startTime, LocalDateTime endTime, Integer hourlyPrice) {
        if (hourlyPrice == null || hourlyPrice <= 0) {
            throw new IllegalArgumentException("价格必须大于0");
        }

        double hours = calculateHours(startTime, endTime);
        
        // 使用 BigDecimal 进行精确计算
        BigDecimal hoursBD = BigDecimal.valueOf(hours);
        BigDecimal priceBD = BigDecimal.valueOf(hourlyPrice);
        
        // 计算总价，保留2位小数，四舍五入
        return hoursBD.multiply(priceBD).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小时数为易读形式
     * 例如: 5.5 -> "5小时30分钟"
     */
    public static String formatHours(double hours) {
        int fullHours = (int) hours;
        int minutes = (int) Math.round((hours - fullHours) * 60);
        
        if (minutes == 0) {
            return fullHours + "小时";
        } else {
            return fullHours + "小时" + minutes + "分钟";
        }
    }

    /**
     * 格式化时间段为显示文本
     * 例如: 2025-12-10 15:00 到 2025-12-10 20:00 -> "12月10日 15:00-20:00"
     */
    public static String formatTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return "";
        }
        
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("M月d日");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        
        String date = startTime.format(dateFormatter);
        String start = startTime.format(timeFormatter);
        String end = endTime.format(timeFormatter);
        
        return date + " " + start + "-" + end;
    }
}
