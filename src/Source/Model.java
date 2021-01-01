package Source;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;

public class Model {

    private ArrayList identificators;

    public Model() {
        identificators = new ArrayList<String>();
        getIdsFromJSON();
    }

    public ArrayList getIdentificators() {
        return identificators;
    }

    public ArrayList<Item> getAllItems() {

        ArrayList returnItems = new ArrayList<Item>();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i y lo transforma en objeto
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    // agarra la clave de esa categoria (su nombre)
                    String clave = json.keySet().toString();
                    String resultado = clearString(clave);

                    // me meto adentro de la categoria
                    JSONObject jsonCatKey = (JSONObject) json.get(resultado);

                    // agarro el stock y lo paso a int
                    Object obj_stock = jsonCatKey.get("stock");
                    int stock = CasterObjToInt(obj_stock);

                    // si hay stock....
                    if (stock != 0) {

                        // agarro el array de items
                        JSONArray items = (JSONArray) jsonCatKey.get("items");

                        // por cada item, le saco la id
                        for (int j = 0; j < items.size(); j++) {
                            // agarro el item
                            JSONObject item = (JSONObject) items.get(j);

                            // lo paso a la clase item
                             Item aux = createItem(item);

                            // le asigno la categoria donde estaba
                             aux.setCategoria(resultado);

                            // y lo guardo
                            returnItems.add(aux);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnItems;
    }

    public void removeItem(Item item) {

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            int index = 0;

            // recorre cada instancia del array
            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();

                String resultado = clearString(clave);

                // si la clave que tengo es igual a la que estoy buscando...
                if (resultado.equals(item.getCategoria())) {
                    index = i;

                    JSONObject category = (JSONObject) json.get(resultado);

                    JSONObject aux_category = new JSONObject();

                    JSONObject aux_int_category = new JSONObject();

                    int stock = 0;

                    // si me da null (error) es porque el stock es 0
                    try {
                        // de obj a int
                        stock = CasterObjToInt(category.get("stock"));
                    } catch (Exception e) {
                        stock = 0;
                    }

                    JSONArray items = (JSONArray) category.get("items");

                    for (int j = 0; j < items.size(); j++) {
                        JSONObject cosa = (JSONObject) items.get(j);

                        String id = (String) cosa.get("id");

                        if (id.equals(item.getId())) {
                            stock--;
                            aux_int_category.put("stock", stock);
                            items.remove(cosa);
                            aux_int_category.put("items", items);

                            aux_category.put(resultado, aux_int_category);

                            jsonArray.remove(index);
                            jsonArray.add(index, aux_category);
                        }
                    }

                    // y finalizo el for
                    i = jsonArray.size();
                }
            }

            // agarro el stock y le sumo el mio
            try (FileWriter file = new FileWriter("src/Source/stock.json")) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getIdsFromJSON() {

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            // recorre cada instancia del array
            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();
                String resultado = clearString(clave);

                // me meto adentro de la categoria
                JSONObject jsonCatKey = (JSONObject) json.get(resultado);

                // agarro el stock y lo paso a int
                Object obj_stock = jsonCatKey.get("stock");
                int stock = CasterObjToInt(obj_stock);

                // si hay stock....
                if (stock != 0) {

                    // agarro el array de items
                    JSONArray items = (JSONArray) jsonCatKey.get("items");

                    // por cada item, le saco la id
                    for (int j = 0; j < items.size(); j++) {
                        JSONObject item = (JSONObject) items.get(j);
                        String id = (String) item.get("id");
                        // y la guardo en el arraylist de ids
                        identificators.add(id);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getCategories() {
        ArrayList categories = new ArrayList<String>();

        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < jsonArray.size(); i++) {

                JSONObject obj = (JSONObject) jsonArray.get(i);
                String clave = obj.keySet().toString();
                String resultado = clearString(clave);
                categories.add(resultado);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Item getItemById(String id) {

        Item retorno = null;

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // cositas para pasarlo a json object
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();

                String resultado = clearString(clave);

                JSONObject jsonCategory = (JSONObject) json.get(resultado);

                JSONArray info = (JSONArray) jsonCategory.get("items");

                for (int x = 0; x < info.size(); x++) {

                    if (info.size() != 0) {
                        JSONObject item = (JSONObject) info.get(x);
                        String item_id = (String) item.get("id");

                        if (id.equals(item_id)) {

                            Item ff = createItem(item);
                            ff.setCategoria(resultado);
                            retorno = ff;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    public void addItem(Item ff, String category) {
        JSONObject parsedItem = parseItem(ff);
        int homeIndex = 0;

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            JSONObject jsonCategory = null;

            // recorre cada instancia del array
            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();

                String resultado = clearString(clave);

                // si la clave que tengo es igual a la que estoy buscando...
                if (resultado.equals(category)) {

                    // guardo esa categoria
                    jsonCategory = json;

                    // guardo donde esta ubicada (para borrarla despues)
                    homeIndex = i;

                    // y finalizo el for
                    i = jsonArray.size();
                }
            }

            int stock;

            // me meto adentro de la categoria
            JSONObject jsonCatKey = (JSONObject) jsonCategory.get(category);

            // si me da null (error) es porque el stock es 0
            try {
                // de obj a int
                stock = CasterObjToInt(jsonCatKey.get("stock"));
            } catch (Exception e) {
                stock = 0;
            }

            // agarro el stock y le sumo el mio
            stock++;

            // agarro los items que estaban antes
            JSONArray itemList = (JSONArray) jsonCatKey.get("items");

            // creo la categoria auxiliar
            JSONObject category_aux = new JSONObject();
            JSONObject categoryData = new JSONObject();

            // agrego el stock anterior al aux
            categoryData.put("stock", stock);

            // agrego el nuevo (o editado) item a la lista
            itemList.add(parsedItem);

            // guardo la lista de items en el aux
            categoryData.put("items", itemList);

            category_aux.put(category, categoryData);

            // remuevo el anterior
            jsonArray.remove(homeIndex);

            // y agrego el nuevo
            jsonArray.add(homeIndex, category_aux);

            // aca ya se escribe al arhivo
            try (FileWriter file = new FileWriter("src/Source/stock.json")) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void createCategory(String category) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // jsonObject es todo el archivo json
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            JSONObject obj = new JSONObject();
            JSONObject list = new JSONObject();

            list.put("stock", 0);

            JSONArray array = new JSONArray();

            list.put("items", array);

            obj.put(category, list);

            // añado esa categoria en el final
            jsonArray.add(obj);

            try (FileWriter file = new FileWriter("src/Source/stock.json")) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject parseItem(Item ff) {
        JSONObject obj = new JSONObject();

        obj.put("id", ff.getId());

        JSONObject details = new JSONObject();
        details.put("venta", ff.getVenta());
        details.put("compra", ff.getCompra());
        details.put("nombre", ff.getNombre());
        details.put("descripcion", ff.getDescripcion());

        obj.put("atributos", details);

        return obj;

    }

    public Item createItem(JSONObject ff) {

        String id = (String) ff.get("id");

        JSONObject info = (JSONObject) ff.get("atributos");

        String descripcion = (String) info.get("descripcion");
        String nombre = (String) info.get("nombre");

        int precioVenta = CasterObjToInt(info.get("venta"));
        int precioCompra = CasterObjToInt(info.get("compra"));

        Item item = new Item(nombre, descripcion, precioVenta, precioCompra);
        item.setId(id);

        return item;
    }

    public ArrayList getStockOfAll() {
        JSONParser parser = new JSONParser();

        ArrayList stockList = new ArrayList<CategoryStock>();

        try (Reader reader = new FileReader("src/Source/stock.json")) {

            // cositas para pasarlo a json object
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();
                String resultado = clearString(clave);

                JSONObject jsonCategory = (JSONObject) json.get(resultado);

                int stock = CasterObjToInt(jsonCategory.get("stock"));

                CategoryStock aux = new CategoryStock(resultado, stock);
                stockList.add(aux);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stockList;
    }

    public int CasterObjToInt(Object ff) {
        Object obj = ff;
        String str = obj.toString();
        int integer = Integer.parseInt(str);

        return integer;
    }

    public String clearString(String ff) {
        String resultado = "";
        for (int j = 0; j < ff.length(); j++) {
            if (!(j == 0 || j == ff.length() - 1)) {
                resultado += ff.charAt(j);
            }
        }
        return resultado;
    }

    public String createId() {

        int id = new Random().nextInt(999999);
        String resultado;
        resultado = "" + id;

        // si no esta ya creado...
        if (identificators.contains(resultado)) {
            while (identificators.contains(resultado)) {
                int aux = new Random().nextInt(999999);
                resultado = "" + aux;
            }
        }

        return resultado;
    }
}
