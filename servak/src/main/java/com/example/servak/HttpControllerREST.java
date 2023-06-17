package com.example.servak;

import com.beust.ah.A;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static java.lang.Integer.parseInt;

@RestController
public class HttpControllerREST extends HttpServlet {

    @RequestMapping("/download_image_type")
    public String uploadImage(@RequestBody byte[] imageBytes, HttpServletRequest request) throws SQLException, ClassNotFoundException, IOException {
        int index = 0;
        ArrayList<Integer> arr = get_all_types_id();
        do {
            if (arr.contains(index)) {
                index++;
            } else break;
        } while (true);
        bytesToImage(imageBytes, "Data/Images/Types/" + index + ".jpg");
        return String.valueOf(add_type(index, request.getParameter("name")));
    }

    @RequestMapping("/download_image_product")
    public String uploadImage_(@RequestBody byte[] imageBytes, HttpServletRequest request) throws SQLException, ClassNotFoundException, IOException {
        int index = 0;
        ArrayList<Integer> arr = get_all_types_id_in_products();
        do {
            if (arr.contains(index)) {
                index++;
            } else break;
        } while (true);
        add_product(Integer.parseInt(request.getParameter("vid")), index, request.getParameter("name"), Double.parseDouble(request.getParameter("price")), Integer.parseInt(request.getParameter("time")));
        return String.valueOf(bytesToImage(imageBytes, "Data/Images/Products/" + index + ".jpg"));
    }

    private boolean add_product(int vid, int index, String name, Double price, int time) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            PreparedStatement pr_stmt = null;
            String sql = "INSERT INTO table_of_products (ID,VID,TYPE,PRICE,TIME) VALUES (?,?,?,?,?)";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setLong(1, index);
            pr_stmt.setInt(2, vid);
            pr_stmt.setString(3, name);
            pr_stmt.setDouble(4, price);
            pr_stmt.setInt(5, time);
            pr_stmt.executeUpdate();
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return true;
    }

    private boolean add_type(int index, String name) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            PreparedStatement pr_stmt = null;
            String sql = "INSERT INTO types_of_products (ID,VID) VALUES (?,?)";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setLong(1, index);
            pr_stmt.setString(2, name);
            pr_stmt.executeUpdate();
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return true;
    }

    @RequestMapping("/type")
    public boolean check_data_types(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "check_name":
                return get_all_types().contains(request.getParameter("name"));
            case "delete_type":
                String[] numbersArray = request.getParameter("str").split(",");
                ArrayList<Integer> numbersList = new ArrayList<>();
                for (String number : numbersArray) {
                    int num = Integer.parseInt(number);
                    numbersList.add(num);
                }
                System.out.println(numbersList);
                deleteTypes(numbersList);
                for (int i : numbersList) {
                    delete_file(i + ".jpg");
                }
                ArrayList<Integer> a = get_all_types_id_in_products();
                if (a.size() != 0) {
                    for (int i : a) {
                        deleteProductsOfType(i);
                    }
                }
                return true;
        }
        return false;
    }

    @RequestMapping("/product")
    public boolean check_data_products(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "check_name":
                return get_all_products().contains(request.getParameter("name"));
            case "delete_product":
                String[] Array_name = request.getParameter("str").split(",");
                String[] Array_id = request.getParameter("id").split(",");

                deleteProduct(Array_name);
                System.out.println(Arrays.toString(Array_id));
                for (String str : Array_id) {
                    delete_file_product(str + ".jpg");
                }
                return true;
        }
        return false;
    }

    @RequestMapping("/order_do")
    public boolean order_do_(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "delete_order":{
                add_data_(Integer.parseInt(request.getParameter("user")),Long.parseLong(request.getParameter("id")), false);
                return deleteORDER(Long.parseLong(request.getParameter("id")));
            }
            case "adm_del_or":{
                System.out.println(request.getParameter("ord"));
                ArrayList<Integer> ids=convertStringToArrayList(request.getParameter("id"));
                ArrayList<Long> id_order=convertStringToArrayList_LONG(request.getParameter("ord"));
                for(int i=0;i<ids.size();i++){
                    add_data_(ids.get(i),id_order.get(i), true);
                    deleteORDER(id_order.get(i));
                }
                return true;
            }
            case "acc":{

                add_data_(Integer.parseInt(request.getParameter("user")),Long.parseLong(request.getParameter("id")), true);
            }

        }
        return false;
    }


    public void add_data_(int user,Long id, boolean get_order) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            PreparedStatement pr_stmt = null;
            String sql = "INSERT INTO data_of_order (id_user, id_order,get_order) VALUES (?,?,?)";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setInt(1, user);
            pr_stmt.setLong(2, id);
            pr_stmt.setBoolean(3, get_order);
            pr_stmt.executeUpdate();
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public ArrayList<Integer> get_all_vid_id_in_products() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products");
            while (rs.next()) {
                data.add(rs.getInt("vid"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    private static ArrayList<Integer> convertStringToArrayList(String numbersString) {
        List<String> numbersStringList = Arrays.asList(numbersString.split(","));
        ArrayList<Integer> numbersList = new ArrayList<>();

        for (String number : numbersStringList) {
            int parsedNumber = Integer.parseInt(number);
            numbersList.add(parsedNumber);
        }

        return numbersList;
    }

    private static ArrayList<Long> convertStringToArrayList_LONG(String numbersString) {
        List<String> numbersStringList = Arrays.asList(numbersString.split(","));
        ArrayList<Long> numbersList = new ArrayList<>();

        for (String number : numbersStringList) {
            numbersList.add(Long.parseLong(number));
        }

        return numbersList;
    }

    private static ArrayList<Double> convertStringToArrayListD(String numbersString) {
        List<String> numbersStringList = Arrays.asList(numbersString.split(","));
        ArrayList<Double> numbersList = new ArrayList<>();

        for (String number : numbersStringList) {
            double n = Double.parseDouble(number);

            numbersList.add(n);
        }

        return numbersList;
    }


    @RequestMapping("/order_make")
    public String make(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "make_order": {
                //user id,   street id,  nameproduct id, sum double;
                ArrayList<HashMap> list_of_map = new ArrayList<>();
                ArrayList<Integer> name_product = convertStringToArrayList(request.getParameter("name_product_ids"));

                ArrayList<Integer> name_prod_type_id = new ArrayList<>();

                ArrayList<Integer> all_vid = get_all_vid_id_in_products();
                ArrayList<Integer> all_product_id = get_all_types_id_in_products();
                for (int n : name_product) {
                    name_prod_type_id.add(all_vid.get(all_product_id.indexOf(n)));
                }
                ArrayList<Integer> kol_Vo = convertStringToArrayList(request.getParameter("kol"));
                ArrayList<Double> summ = convertStringToArrayListD(request.getParameter("sum"));

                for (int i = 0; i < name_product.size(); i++) {
                    HashMap<String, String> map = new HashMap<>();

                    map.put("type", String.valueOf(name_prod_type_id.get(i)));
                    map.put("product", String.valueOf(name_product.get(i)));
                    map.put("sum", String.valueOf(summ.get(i)));
                    map.put("kol_vo", String.valueOf(kol_Vo.get(i)));


                    list_of_map.add(map);
                }

                ArrayList<String> name = get_all_phones();
                ArrayList<Integer> name_id = get_all_phones_id();
                int user = name_id.get(name.indexOf(request.getParameter("user")));

                return make_ord(user, Integer.parseInt(request.getParameter("street")), list_of_map);
            }

            default:
                return "error";
        }
    }

    public ArrayList<Integer> get_street_id() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<Integer> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "name_street");
            while (rs.next()) {
                int id = rs.getInt("id");
                id_tt.add(id);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id_tt;
    }

    public ArrayList<String> get_street_name() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "name_street");
            while (rs.next()) {
                String id = rs.getString("Street");
                id_tt.add(id);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id_tt;
    }

    public ArrayList<Long> get_unik_id() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<Long> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "orders");
            while (rs.next()) {
                Long id = rs.getLong("id_order");
                id_tt.add(id);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id_tt;
    }

    public long randrom_l() {
        long min = 1000000000L; // Минимальное 10-значное число (10^9)
        long max = 9999999999L; // Максимальное 10-значное число (10^10 - 1)

        Random random = new Random();
        return min + ((long) (random.nextDouble() * (max - min)));
    }

    private String make_ord(int user, int street, ArrayList<HashMap> list) throws ClassNotFoundException, SQLException {
        long id = randrom_l();
        ArrayList<Long> all_id = get_unik_id();
        if (all_id.size() != 0) {
            do {
                id = randrom_l();
            } while (all_id.contains(id));
        }
        ///

        ///

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            PreparedStatement pr_stmt = null;
            for (HashMap hashMap : list) {
                String sql = "INSERT INTO orders (id_order,Street,id_user,TYPE,PRODUCT,kol_vo,summ) VALUES (?,?,?,?,?,?,?)";
                pr_stmt = c.prepareStatement(sql);
                pr_stmt.setLong(1, id);
                pr_stmt.setInt(2, street);
                pr_stmt.setInt(3, user);

                pr_stmt.setInt(4, Integer.parseInt((String) hashMap.get("type")));
                pr_stmt.setInt(5, Integer.parseInt((String) hashMap.get("product")));
                pr_stmt.setDouble(6, Double.parseDouble((String) hashMap.get("kol_vo")));
                pr_stmt.setDouble(7, Double.parseDouble((String) hashMap.get("sum")));
                pr_stmt.executeUpdate();
            }
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return String.valueOf(id);
    }

    @RequestMapping("/order")
    public ResponseEntity<String> order(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "get_adresa": {
                ArrayList<String> products = get_all_adres();
                return ResponseEntity.ok().body(new Gson().toJson(products));
            }
            case "get_ors": {
                ArrayList<Integer> id = get_all_phones_id();
                ArrayList<String> name = get_all_phones();
                int i = name.indexOf(request.getParameter("user"));
                return ResponseEntity.ok().body(new Gson().toJson(get_all_orders(id.get(i))));
            }
            case "pr_of_order": {
                ArrayList<Integer> arr1 = get_prd(Long.valueOf(request.getParameter("id")));

                ArrayList<String> id_pr = get_all_product();
                ArrayList<Integer> id_id = get_all_product_id();

                ArrayList<String> arrrr = new ArrayList<>();

                for (int j = 0; j < arr1.size(); j++) {
                    int index = id_id.indexOf(arr1.get(j));
                    arrrr.add(id_pr.get(index));
                }
                System.out.println(arrrr);
                return ResponseEntity.ok().body(new Gson().toJson(arrrr));
            }
            case "all":{
                ArrayList<String> arr = get_all_orders_all();
                return ResponseEntity.ok().body(new Gson().toJson(arr));
            }


            case "get_size_order": {
                System.out.println("1111111111111111111111");
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(get_kol_zakazov(Long.parseLong(request.getParameter("id")), true));
                arr.add(get_kol_zakazov(Long.parseLong(request.getParameter("id")), false));
                return ResponseEntity.ok().body(new Gson().toJson(arr));
            }

            default:
                ArrayList<String> arr = new ArrayList<>();
                arr.add("0");
                return ResponseEntity.ok().body(new Gson().toJson(arr));
        }
    }

    public ArrayList<Integer> get_prd(Long ord) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> data = new ArrayList<>();
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders Where id_order=" + "'" + ord + "'");
            while (rs.next()) {
                data.add(rs.getInt("product"));
                System.out.println("!!!!!!!!!!!!" + rs.getInt("product"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return data;
    }

    public int get_kol_zakazov(long id, boolean b) throws ClassNotFoundException, SQLException {
        ArrayList<Long> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM data_of_order WHERE id_user=" + "'" + id + "'" + " AND get_order=" + "'" + b + "'");
            while (rs.next()) {
                data.add(rs.getLong("id_order"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data.size();
    }

    private boolean deleteORDER(long id) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement statement = null;
        System.out.println("!!!!!!!!!!!!!!"+id);
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                    "denis21042", "JfWRQ5PG9iKn");
            String query = "DELETE FROM orders WHERE id_order = ?";
            statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return true;
    }

    private void deleteProductsOfType(int id) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                    "denis21042", "JfWRQ5PG9iKn");
            String query = "DELETE FROM table_of_products WHERE ID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }


    private boolean delete_file(String imageName) {
        String directoryPath = "Data/Images/Types/"; // Путь к директории с изображениями
        File directory = new File(directoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(imageName)) {
                        return file.delete();
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private boolean delete_file_product(String imageName) {
        String directoryPath = "Data/Images/Products/"; // Путь к директории с изображениями
        File directory = new File(directoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(imageName)) {
                        return file.delete();
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    @RequestMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImage(HttpServletRequest request) throws IOException {
        switch (request.getParameter("what_do")) {
            case "images_menu" -> {//главное меню
                byte[] imageBytes = imageToBytes("Data/Images/Types/" + request.getParameter("number") + ".jpg");
                System.out.println(request.getParameter("number"));
                return ResponseEntity.ok().body(imageBytes);
            }

            case "images_products" -> {//выбор товара
                byte[] imageBytes = imageToBytes("Data/Images/Products/" + request.getParameter("number") + ".jpg");
                System.out.println("Data/Images/Products/" + request.getParameter("number") + ".jpg");
                return ResponseEntity.ok().body(imageBytes);
            }
            case "qr_code" -> {
                new QRCodeGenerator().qr_code(Long.parseLong(request.getParameter("num")));
                byte[] imageBytes = imageToBytes("Data/Images/QR_CODE/" + request.getParameter("num") + ".jpg");
                return ResponseEntity.ok().body(imageBytes);
            }
            default -> {
                byte[] imageBytes = imageToBytes("Data/Images/Errors/" + 0 + ".jpg");
                return ResponseEntity.ok().body(imageBytes);
            }
        }
    }

    public static byte[] imageToBytes(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public boolean bytesToImage(byte[] imageBytes, String outputPath) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

        File outputFile = new File(outputPath);
        ImageIO.write(bufferedImage, "jpg", outputFile);
        return true;
    }

    public ArrayList<Integer> get_all_types_id_in_products() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products");
            while (rs.next()) {
                data.add(rs.getInt("id"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<Integer> get_all_types_id() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM types_of_products");
            while (rs.next()) {
                data.add(rs.getInt("id"));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<String> get_all_types() throws ClassNotFoundException, SQLException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM types_of_products");
            while (rs.next()) {
                data.add(rs.getString("vid"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<Integer> get_all_product_id() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products");
            while (rs.next()) {
                data.add(rs.getInt("id"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<String> get_all_product() throws ClassNotFoundException, SQLException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products");
            while (rs.next()) {
                data.add(rs.getString("type"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<String> get_all_from_types() throws ClassNotFoundException, SQLException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM types_of_products");
            while (rs.next()) {
                data.add(String.valueOf(rs.getInt("id")));
                data.add(rs.getString("vid"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    @RequestMapping("/data")
    public ResponseEntity<String> post_data(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "number_of_products":
                ArrayList<String> products = get_num_of_products(request.getParameter("num"));
                return ResponseEntity.ok().body(new Gson().toJson(products));
            case "get_id":
                ArrayList<String> id = get_id_of_products(request.getParameter("num"));
                return ResponseEntity.ok().body(new Gson().toJson(id));
            case "gettypes":
                ArrayList<String> types = get_all_from_types();
                return ResponseEntity.ok().body(new Gson().toJson(types));
            case "g":
                return ResponseEntity.ok().body(new Gson().toJson(get_id_of_products_one(request.getParameter("num"))));
            default:
                ArrayList<String> arr = new ArrayList<>();
                arr.add("0");
                return ResponseEntity.ok().body(new Gson().toJson(arr));
        }
    }


    private void deleteTypes(ArrayList<Integer> ids) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                    "denis21042", "JfWRQ5PG9iKn");
            String query = "DELETE FROM types_of_products WHERE ID = ?";
            statement = connection.prepareStatement(query);

            for (int id : ids) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void deleteProduct(String[] str) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                    "denis21042", "JfWRQ5PG9iKn");
            String query = "DELETE FROM table_of_products WHERE TYPE = ?";
            statement = connection.prepareStatement(query);

            for (String id : str) {
                statement.setString(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @RequestMapping("/auth")
    public String index(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "check_and_add_data" -> {
                boolean cont_ = contains_phone_in_BD(request.getParameter("phone"));
                if (cont_ == true) {
                    return "already";
                } else {
                    add_data(request.getParameter("name"), (request.getParameter("phone")), request.getParameter("pass"));
                    return "good";
                }
            }
            case "enter_chk" -> {
                //System.out.println(request.getParameter("phone")+" "+ request.getParameter("pass"));
                return check_pass_log_user(request.getParameter("phone"), request.getParameter("pass"), "table_of_users");
            }
            case "admin_chk" -> {
                System.out.println(request.getParameter("phone") + " " + request.getParameter("pass"));
                return check_pass_log_admin(request.getParameter("phone"), request.getParameter("pass"), "table_of_admins");
            }
            case "number_of_types" -> {
                return get_numbers_lines("types_of_products", "vid");
            }
            default -> {
                return "error";
            }
        }
    }

    private ArrayList<String> get_num_of_products(String str) throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products WHERE vid='" + Integer.parseInt(str) + "'");
            while (rs.next()) {
                data.add(String.valueOf(rs.getInt("id")));
                data.add(rs.getString("type"));
                data.add(String.valueOf(rs.getFloat("price")));
                data.add(String.valueOf(rs.getInt("time")));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;

    }


    private ArrayList<String> get_all_adres() throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM name_street");
            while (rs.next()) {
                data.add(rs.getString("id"));
                data.add(rs.getString("Street"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return data;

    }

    private ArrayList<String> get_all_orders(int id) throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders WHERE id_user=" + "'" + id + "'");
            while (rs.next()) {
                data.add(rs.getString("id_order"));
                data.add(String.valueOf(rs.getDouble("summ")));
                data.add(String.valueOf(rs.getInt("product")));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return data;

    }

    private ArrayList<String> get_all_orders_all() throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders");
            while (rs.next()) {
                data.add(String.valueOf((rs.getInt("id_user"))));
                data.add(rs.getString("id_order"));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return data;

    }

    private ArrayList<String> get_all_products() throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products");
            while (rs.next()) {
                data.add(rs.getString("type"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return data;

    }

    private ArrayList<String> get_id_of_products(String str) throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products WHERE id='" + Integer.parseInt(str) + "'");
            while (rs.next()) {
                data.add(String.valueOf(rs.getInt("id")));
                data.add(rs.getString("type"));
                data.add(String.valueOf(rs.getFloat("price")));
                data.add(String.valueOf(rs.getInt("time")));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;

    }

    private ArrayList<String> get_id_of_products_one(String str) throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products WHERE id='" + Integer.parseInt(str) + "'");
            while (rs.next()) {
                data.add(str);
                data.add(rs.getString("type"));
                data.add(String.valueOf(rs.getInt("time")));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;

    }


    private String get_numbers_lines(String t_n, String name_column) throws ClassNotFoundException, SQLException {
        int c1 = 0;
        ArrayList<String> arrayList = new ArrayList<>();
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
            while (rs.next()) {
                String tt = rs.getString(name_column);
                if (!arrayList.contains(tt)) arrayList.add(tt);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return String.valueOf(arrayList.size());
    }

    public void add_data(String name, String phone, String password) {
        Thread thread = new Thread(() -> {
            try {
                long id = 0;
                ArrayList<Long> all_id = get_all_id("table_of_users");
                if (all_id.size() != 0) {
                    id--;
                    do {
                        id++;
                    } while (all_id.contains(id));
                }
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                        "denis21042", "JfWRQ5PG9iKn");
                try {
                    PreparedStatement pr_stmt = null;
                    String sql = "INSERT INTO table_of_users (ID,NAME_USER,PHONE,PASSWORD_OF_USER) VALUES (?,?,?,?)";
                    pr_stmt = c.prepareStatement(sql);
                    pr_stmt.setLong(1, id);
                    pr_stmt.setString(2, name);
                    pr_stmt.setString(3, phone);
                    pr_stmt.setString(4, password);
                    pr_stmt.executeUpdate();
                    pr_stmt.close();
                    c.close();
                } catch (Exception e) {
                    System.out.println("Ошибка во внесении данных в БД");
                    e.printStackTrace();
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    public boolean contains_phone_in_BD(String phone) {
        try {
            return get_all_phones().contains(phone);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String check_pass_log_admin(String login, String pass, String table) {
        try {
            System.out.println(get_all_logins().contains(login) + " " + get_all_password(table).contains(pass));
            if (get_all_logins().contains(login) && get_all_password(table).contains(pass)) {
                return "ok";
            } else if (!get_all_logins().contains(login) && get_all_password(table).contains(pass)) {
                return "ph_no";
            } else if (get_all_logins().contains(login) && !get_all_password(table).contains(pass)) {
                return "pass_no";
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("11111");
            return "error";
        }
        return "error";
    }

    public String check_pass_log_user(String phone, String pass, String table) {
        try {
            //return get_all_phones().contains(phone) && get_all_password(table).contains(pass);
            System.out.println(get_all_phones().contains(phone) + " " + get_all_password(table).contains(pass));
            if (get_all_phones().contains(phone) && get_all_password(table).contains(pass)) {
                return "ok";
            } else if (!get_all_phones().contains(phone) && get_all_password(table).contains(pass)) {
                return "ph_no";
            } else if (get_all_phones().contains(phone) && !get_all_password(table).contains(pass)) {
                return "pass_no";
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "error";
        }
        return "error";
    }

    private ArrayList<String> get_all_password(String t_n) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> pass_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
            while (rs.next()) {
                String login = rs.getString("PASSWORD_OF_USER");
                pass_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return pass_tt;
    }

    private ArrayList<String> get_all_logins() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> login_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "table_of_admins");
            while (rs.next()) {
                String login = rs.getString("LOGIN_OF_USER");
                login_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return login_tt;
    }

    private ArrayList<String> get_all_phones() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> login_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "table_of_users");
            while (rs.next()) {
                String login = rs.getString("PHONE");
                login_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return login_tt;
    }

    private ArrayList<Integer> get_all_phones_id() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<Integer> login_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "table_of_users");
            while (rs.next()) {
                int login = rs.getInt("id");
                login_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return login_tt;
    }

    private ArrayList<Long> get_all_id(String t_n) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<Long> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
            while (rs.next()) {
                Long id = rs.getLong("ID");
                id_tt.add(id);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id_tt;
    }


}

