package nro.item;

import java.sql.*;
import java.util.ArrayList;

import nro.main.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import nro.main.Util;

import nro.part.Part;
import nro.part.PartImage;

public class ItemDAO {

    public static Item load(int itemId) {
        Item item = null;
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM item WHERE id=? LIMIT 1");
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                item = new Item();
                item.quantity = rs.getInt("quantity");
                item.template = ItemTemplates.get(rs.getShort("id"));
            }
            ps = conn.prepareStatement("SELECT * FROM item WHERE id=?");
            ps.setInt(1, itemId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemOption option = new ItemOption();
                option.optionTemplate = ItemData.iOptionTemplates[rs.getInt("option_id")];
                option.param = rs.getShort("param");
                if (item != null) {
                    item.itemOptions.add(option);
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return item;
    }
    

    public static Part[] readPartsFromDB() {
        String SELECT_LOG = "SELECT * FROM parts";
        PreparedStatement ps;
        ArrayList<Part> partList = new ArrayList<Part>();
        try {
            Connection conn = DataSource.getConnection();
            ps = conn.prepareStatement(SELECT_LOG);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int i = rs.getInt("id");
                byte type = (byte) rs.getInt("type");
                String piStr = rs.getString("pi");
                JSONArray jarr = (JSONArray) JSONValue.parse(piStr);
                Part part = new Part(type);
                for (int j = 0; j < jarr.size(); j++) {
                    JSONObject piObj = (JSONObject) jarr.get(j);
                    PartImage pi = new PartImage();
                    pi.id = ((Long) piObj.get("id")).shortValue();
                    pi.dx = ((Long) piObj.get("dx")).byteValue();
                    pi.dy = ((Long) piObj.get("dy")).byteValue();
                    part.pi[j] = pi;
                    Util.log("PART " + i + ": ID: " + part.pi[j].id + ", dx: " + part.pi[j].dx + ", dy: " + part.pi[j].dy);
                }
                partList.add(part);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partList.toArray(new Part[partList.size()]);
    }
    

    public static void insertItemDB(int id, byte type, byte gender, String name, String description, byte level, int strRequire, int iconID, short part) {
        String INSERT_LOG = "INSERT INTO item (id, type, gender, name, description, level, strRequire, iconID, part) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps;
        try {
            Connection conn = DataSource.getConnection();
            ps = conn.prepareStatement(INSERT_LOG, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            ps.setInt(1, id);
            ps.setInt(2, (int)type);
            ps.setInt(3, (int)gender);
            ps.setString(4, name);
            ps.setString(5, description);
            ps.setInt(6, (int)level);
            ps.setInt(7, (int)strRequire);
            ps.setInt(8, iconID);
            ps.setInt(9, (int)part);
            ps.executeUpdate();
            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
