package com.aura.dao;

import com.aura.db.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JavaDBDao {

    private static final String SAVE_CONTENT_COUNT =
            "INSERT INTO sparkcore_content_data(contentid,`day`,pv,uv) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE pv = values(pv),uv = values(uv)";
    private static final String SAVE_CONTENT_DETAIL =
            "INSERT INTO sparkcore_content_detail(contentid,url,title) VALUES (?,?,?) ON DUPLICATE KEY UPDATE url = values(url),title = values(title)";
    private static final String SAVE_DIMENSION_COUNT =
            "INSERT INTO sparkcore_dimension_data(dimeid,`day`,pv,uv,ip) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE pv = values(pv),uv = values(uv),ip = values(ip)";
    private static final String UPDATE_DIMENSION_TIME =
            "INSERT INTO sparkcore_dimension_data(dimeid,`day`,time) VALUES (?,?,?) ON DUPLICATE KEY UPDATE time = values(time)";
    private static final String GET_DIMENSION_MAP_BY_TYPE =
            "SELECT id dimeId,`value` FROM common_dimension WHERE `type` = ?";
    private static final String SAVE_GENDER_COUNT =
            "INSERT INTO mllib_gender_data(genderid,`day`,pv,uv,ip) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE pv = values(pv),uv = values(uv),ip = values(ip)";
    private static final String  SAVE_CHANNEL_COUNT =
            "INSERT INTO mllib_channel_data(channelid,`day`,pv,uv,ip) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE pv = values(pv),uv = values(uv),ip = values(ip)";
    private static final String SAVE_STREAMING_DIMENSION_COUNT =
            "INSERT INTO streaming_dimension_data(dimeid,`second`,pv,uv) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE pv = values(pv),uv = values(uv)";

    private static void execute(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.execute();
        } finally {
            DBHelper.close(pstmt);
        }
    }

    public static void saveContentCount(Connection conn, long contentId, String day, long pv, long uv) throws SQLException {
        execute(conn, SAVE_CONTENT_COUNT, contentId, day, pv, uv);
    }

    public static void saveContentDetail(Connection conn, long contentId, String url, String title) throws SQLException {
        execute(conn, SAVE_CONTENT_DETAIL, contentId, url, title);
    }

    public static void saveDimensionCount(Connection conn, int dimId, String day, long pv, long uv, long ip) throws SQLException {
        execute(conn, SAVE_DIMENSION_COUNT, dimId, day, pv, uv, ip);
    }

    public static void updateDimensionTime(Connection conn, int dimId, String day, long time) throws SQLException {
        execute(conn, UPDATE_DIMENSION_TIME, dimId, day, time);
    }

    public static void saveGenderCount(Connection conn, int genderId, String day, long pv, long uv, long ip) throws SQLException {
        execute(conn, SAVE_GENDER_COUNT, genderId, day, pv, uv, ip);
    }

    public static void saveChannelCount(Connection conn, int channelId, String day, long pv, long uv, long ip) throws SQLException {
        execute(conn, SAVE_CHANNEL_COUNT, channelId, day, pv, uv, ip);
    }

    public static void saveStreamingDimensionCount(Connection conn, int dimId, int second, long pv, long uv) throws SQLException {
        execute(conn, SAVE_STREAMING_DIMENSION_COUNT, dimId, second, pv, uv);
    }

    private static Map<String, Integer> getDimensionValuesByType(Connection conn, String type) {
        Map<String, Integer> dimMap = new HashMap<String, Integer>();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(GET_DIMENSION_MAP_BY_TYPE);
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dimMap.put(rs.getString(2), rs.getInt(1));
            }
            return dimMap;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            DBHelper.close(pstmt);
        }
    }

    public static Map<String, Integer> getCountryMap() {
        Connection conn = DBHelper.getConnection();
        return getDimensionValuesByType(conn, "country");
    }

    public static Map<String, Integer> getProvinceMap() {
        Connection conn = DBHelper.getConnection();
        return getDimensionValuesByType(conn, "province");
    }
}
