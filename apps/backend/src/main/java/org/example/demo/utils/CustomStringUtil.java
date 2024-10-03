package org.example.demo.utils;

import org.springframework.stereotype.Component;

/**
 * Created time 9/30/2024 11:45
 * The type Staff response dto.
 *
 * @author PHAH04
 * Vui lòng ......
 */
@Component
public class CustomStringUtil {
    public String formatUnderscore (String key){
        if (key != null && key.equals("_")){
            return "\\_";
        }
        return key;
    }

    public String formatSearchParam(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return "%" + name.toLowerCase() + "%";
    }

}
