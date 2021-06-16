package com.lookballs.pictureselector.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 解决intent传递数据量超过1M导致Activity无法启动问题
 */
public class SaveDataHelper {

    private static SaveDataHelper instance;

    public static SaveDataHelper getInstance() {
        if (instance == null) {
            synchronized (SaveDataHelper.class) {
                if (instance == null) {
                    instance = new SaveDataHelper();
                }
            }
        }
        return instance;
    }

    private Map<String, Object> map = new HashMap<>();

    /**
     * 数据存储
     *
     * @param key
     * @param object
     */
    public void saveData(String key, Object object) {
        map.put(key, object);
    }

    /**
     * 获取数据
     *
     * @param key
     * @return
     */
    public Object getData(String key) {
        return map.get(key);
    }

    /**
     * 清除数据
     *
     * @param key
     * @return
     */
    public void clearData(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }

}