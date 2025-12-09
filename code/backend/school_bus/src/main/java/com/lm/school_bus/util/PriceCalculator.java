package com.lm.school_bus.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 价格计算工具类
 * 根据使用时间格式 "12月28日 15:29-20:29" 计算用车时长和价格
 */
public class PriceCalculator {

    /**
     * 从使用时间字符串中解析出开始时间和结束时间
     * 格式: "12月28日 15:29-20:29"
     */
    public static double calculateHours(String usageTime) {
        if (usageTime == null || usageTime.trim().isEmpty()) {
            throw new IllegalArgumentException("使用时间不能为空");
        }

        // 提取时间部分 "15:29-20:29"
        Pattern pattern = Pattern.compile("(\\d{1,2}):(\\d{2})-(\\d{1,2}):(\\d{2})");
        Matcher matcher = pattern.matcher(usageTime);

        if (!matcher.find()) {
            throw new IllegalArgumentException("时间格式不正确，应为：月日 时:分-时:分");
        }

        try {
            int startHour = Integer.parseInt(matcher.group(1));
            int startMinute = Integer.parseInt(matcher.group(2));
            int endHour = Integer.parseInt(matcher.group(3));
            int endMinute = Integer.parseInt(matcher.group(4));

            // 计算总分钟数
            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            // 如果结束时间小于开始时间，说明跨天了
            if (endTotalMinutes < startTotalMinutes) {
                endTotalMinutes += 24 * 60; // 加一天的分钟数
            }

            int durationMinutes = endTotalMinutes - startTotalMinutes;

            // 转换为小时（保留小数）
            return durationMinutes / 60.0;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("时间格式解析失败");
        }
    }

    /**
     * 计算订单价格
     * @param usageTime 使用时间字符串
     * @param hourlyPrice 每小时价格
     * @return 总价格
     */
    public static BigDecimal calculatePrice(String usageTime, Integer hourlyPrice) {
        if (hourlyPrice == null || hourlyPrice <= 0) {
            throw new IllegalArgumentException("价格必须大于0");
        }

        double hours = calculateHours(usageTime);
        
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
}
