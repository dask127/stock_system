package Source;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.Reader;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Model {

    private ArrayList<String> identificators;
    private ArrayList<String> logIdentificators;
    private String path = "src/Source/stock.json";
    private String c = "stock.json";

    public Model() {
        identificators = new ArrayList<String>();
        logIdentificators = new ArrayList<String>();
        getIdsFromJSON();

        // activar si se compila
        // path = c;
    }

    public ArrayList getIdentificators() {
        return identificators;
    }

    public ArrayList<Item> getAllItems() {

        ArrayList returnItems = new ArrayList<Item>();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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

    public int getQuantityOfItem(Item item) {

        int retorno_cantidad = 0;
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            // recorre cada instancia del array
            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();
                String resultado = clearString(clave);

                if (resultado.equals(item.getCategoria())) {

                    // me meto adentro de la categoria
                    JSONObject jsonCatKey = (JSONObject) json.get(resultado);

                    // agarro el array de items
                    JSONArray items = (JSONArray) jsonCatKey.get("items");

                    for (int j = 0; j < items.size(); j++) {

                        // por cada item, le saco la id+
                        JSONObject item_aux = (JSONObject) items.get(j);
                        String id = (String) item_aux.get("id");

                        if (id.equals(item.getId())) {
                            JSONObject atributos = (JSONObject) item_aux.get("atributos");
                            int cantidad = CasterObjToInt(atributos.get("cantidad"));
                            retorno_cantidad = cantidad;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retorno_cantidad;
    }

    public Item sellItem(Item item, int cantidad) {
        item.setCantidad(cantidad);

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < jsonArray.size(); i++) {

                JSONObject objectCategory = (JSONObject) jsonArray.get(i);

                String clave = objectCategory.keySet().toString();
                clave = clearString(clave);

                if (clave.equals(item.getCategoria())) {
                    JSONObject category = (JSONObject) objectCategory.get(clave);

                    int category_stock = CasterObjToInt(category.get("stock"));

                    // resultado seria el stock menos la cantidad de items que pido
                    int resultado = category_stock - cantidad;

                    // aca arranco la nueva categoria para agregar despues
                    JSONObject new_category = new JSONObject();

                    // wrapper va a ser el exterior de la categoria, teniendo por ejemplo el nombre
                    JSONObject new_category_wrapper = new JSONObject();

                    new_category.put("alertar", CasterObjToInt(category.get("alertar")));

                    if (resultado == 0) {

                        new_category.put("stock", 0);
                        new_category.put("items", new JSONArray());

                        new_category_wrapper.put(clave, new_category);

                        jsonArray.remove(i);
                        jsonArray.add(i, new_category_wrapper);

                    } else {

                        new_category.put("stock", resultado);

                        category.put("stock", resultado);

                        JSONArray category_items = (JSONArray) category.get("items");

                        for (int j = 0; j < category_items.size(); j++) {
                            JSONObject category_item = (JSONObject) category_items.get(j);

                            String item_id = (String) category_item.get("id");
                            if (item_id.equals(item.getId())) {

                                JSONObject item_atributes = (JSONObject) category_item.get("atributos");

                                int item_quantity = CasterObjToInt(item_atributes.get("cantidad"));

                                int item_result = item_quantity - cantidad;

                                if (item_result == 0) {

                                    category_items.remove(j);
                                    category.put("items", category_items);

                                } else {

                                    // linea de prueba a ver si modifica, de 2 a 1
                                    item_atributes.put("cantidad", item_result);

                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            // agarro el stock y le sumo el mio
            try (FileWriter file = new FileWriter(path)) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return item;
    }

    public void modifyCategory(String categoryBefore, String categoryAfter) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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
                if (resultado.equals(categoryBefore)) {

                    JSONObject category = (JSONObject) json.get(resultado);

                    JSONObject aux_category = new JSONObject();

                    JSONObject aux_int_category = new JSONObject();

                    int stock = CasterObjToInt(category.get("stock"));

                    JSONArray items = (JSONArray) category.get("items");

                    int alerta = CasterObjToInt(category.get("alertar"));

                    aux_int_category.put("stock", stock);
                    aux_int_category.put("alertar", alerta);
                    aux_int_category.put("items", items);

                    aux_category.put(categoryAfter, aux_int_category);

                    jsonArray.remove(i);
                    jsonArray.add(i, aux_category);
                }
            }

            // agarro el stock y le sumo el mio
            try (FileWriter file = new FileWriter(path)) {
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

    public void modifyItem(Item itemBefore, Item itemAfter) {

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            int index = 0;

            if (itemBefore.getCategoria().equals(itemAfter.getCategoria())) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i y lo transforma en objeto
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    // agarra la clave de esa categoria (su nombre)
                    String clave = json.keySet().toString();

                    String resultado = clearString(clave);

                    // si la clave que tengo es igual a la que estoy buscando...
                    if (resultado.equals(itemBefore.getCategoria())) {
                        index = i;

                        JSONObject category = (JSONObject) json.get(resultado);

                        JSONObject aux_category = new JSONObject();

                        JSONObject aux_int_category = new JSONObject();

                        // """teoricamente""" ya hay stock
                        int stock = CasterObjToInt(category.get("stock"));

                        stock = stock - itemBefore.getCantidad() + itemAfter.getCantidad();

                        JSONArray items = (JSONArray) category.get("items");

                        for (int j = 0; j < items.size(); j++) {
                            JSONObject cosa = (JSONObject) items.get(j);

                            String id = (String) cosa.get("id");

                            if (id.equals(itemBefore.getId())) {

                                JSONObject formattedItem = parseItem(itemAfter);

                                items.remove(j);
                                items.add(j, formattedItem);

                                // aca crea una nueva categoria copia
                                aux_int_category.put("stock", stock);

                                int alerta = CasterObjToInt(category.get("alertar"));
                                aux_int_category.put("alertar", alerta);

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
            } else {
                int firstCategoryIndex = 0;
                int secondCategoryIndex = 0;

                // -----------------------------------------
                // agarro el index de la categoria anterior

                // queda feo, pero asi saco donde se ubica cada categoria
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i y lo transforma en objeto
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    // agarra la clave de esa categoria (su nombre)
                    String clave = json.keySet().toString();

                    String resultado = clearString(clave);

                    if (resultado.equals(itemBefore.getCategoria())) {
                        firstCategoryIndex = i;
                        i = jsonArray.size();
                    }
                }

                // -----------------------------------------
                // agarro el index de la categoria nueva
                for (int j = 0; j < jsonArray.size(); j++) {
                    // agarra ese lugarcito donde está i y lo transforma en objeto
                    JSONObject json = (JSONObject) jsonArray.get(j);

                    // agarra la clave de esa categoria (su nombre)
                    String clave = json.keySet().toString();

                    String resultado = clearString(clave);

                    if (resultado.equals(itemAfter.getCategoria())) {
                        secondCategoryIndex = j;
                        j = jsonArray.size();
                    }
                }

                // ------------------------------------------------------------
                // agarra ese lugarcito donde está i y lo transforma en objeto

                JSONObject json = (JSONObject) jsonArray.get(firstCategoryIndex);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();
                String resultado = clearString(clave);

                JSONObject category = (JSONObject) json.get(resultado);
                JSONObject aux_category = new JSONObject();
                JSONObject aux_int_category = new JSONObject();

                // """teoricamente""" ya hay stock
                int stock = CasterObjToInt(category.get("stock"));
                stock = stock - itemBefore.getCantidad();

                JSONArray items = (JSONArray) category.get("items");

                for (int j = 0; j < items.size(); j++) {
                    JSONObject cosa = (JSONObject) items.get(j);

                    String id = (String) cosa.get("id");

                    if (id.equals(itemBefore.getId())) {

                        items.remove(j);

                        // aca crea una nueva categoria copia
                        aux_int_category.put("stock", stock);

                        int alerta = CasterObjToInt(category.get("alertar"));
                        aux_int_category.put("alertar", alerta);

                        aux_int_category.put("items", items);

                        aux_category.put(resultado, aux_int_category);

                        jsonArray.remove(firstCategoryIndex);
                        jsonArray.add(firstCategoryIndex, aux_category);
                    }
                }

                // aca entra a la nueva categoria y mete el objecto

                JSONObject new_json = (JSONObject) jsonArray.get(secondCategoryIndex);

                clave = new_json.keySet().toString();
                resultado = clearString(clave);

                category = (JSONObject) new_json.get(resultado);

                // aca lo rompe
                JSONObject new_category = new JSONObject();
                JSONObject new_int_category = new JSONObject();

                stock = CasterObjToInt(category.get("stock"));

                stock = stock + itemAfter.getCantidad();

                JSONArray new_items = (JSONArray) category.get("items");

                new_items.add(parseItem(itemAfter));

                // aca crea una nueva categoria copia
                new_int_category.put("stock", stock);

                int alerta = CasterObjToInt(category.get("alertar"));
                new_int_category.put("alertar", alerta);
                new_int_category.put("items", new_items);

                new_category.put(resultado, new_int_category);

                jsonArray.remove(secondCategoryIndex);
                jsonArray.add(secondCategoryIndex, new_category);
            }

            try (

                    FileWriter file = new FileWriter(path)) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void removeItem(Item item) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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

                    // """teoricamente""" ya hay stock
                    int stock = CasterObjToInt(category.get("stock"));

                    JSONArray items = (JSONArray) category.get("items");

                    for (int j = 0; j < items.size(); j++) {
                        JSONObject cosa = (JSONObject) items.get(j);

                        String id = (String) cosa.get("id");

                        if (id.equals(item.getId())) {

                            JSONObject atributos = (JSONObject) cosa.get("atributos");
                            int cantidad_item = CasterObjToInt(atributos.get("cantidad"));

                            // si va a vender lo ultimo que le queda, elimina el producto y
                            // deja el stock en 0
                            stock = stock - cantidad_item;

                            // aca crea una nueva categoria copia
                            aux_int_category.put("stock", stock);

                            int alerta = CasterObjToInt(category.get("alertar"));
                            aux_int_category.put("alertar", alerta);

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
            try (FileWriter file = new FileWriter(path)) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> getItemsByName(String nombre_buscado) {

        ArrayList retorno = new ArrayList<Item>();

        nombre_buscado = nombre_buscado.toUpperCase();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // cositas para pasarlo a json object
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();

                String resultado = clearString(clave);

                JSONObject jsonCategory = (JSONObject) json.get(resultado);

                JSONArray category_items = (JSONArray) jsonCategory.get("items");

                for (int x = 0; x < category_items.size(); x++) {

                    if (category_items.size() != 0) {

                        JSONObject item = (JSONObject) category_items.get(x);
                        JSONObject info = (JSONObject) item.get("atributos");
                        String name = (String) info.get("nombre");

                        name = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        name = name.toUpperCase();

                        if (nombre_buscado.contains(name)) {
                            Item reformatted_item = createItem(item);
                            retorno.add(reformatted_item);
                        } else if (name.contains(nombre_buscado)) {
                            Item reformatted_item = createItem(item);
                            retorno.add(reformatted_item);
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

    public void removeCategory(String categoria) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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
                if (resultado.equals(categoria)) {

                    jsonArray.remove(i);
                    break;
                }
            }

            // agarro el stock y le sumo el mio
            try (FileWriter file = new FileWriter(path)) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getIdsFromJSON() {

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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

    public ArrayList<Item> getItemsByCategory(String categoria) {

        ArrayList retorno = new ArrayList<Item>();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            // recorre cada instancia del array
            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                // agarra la clave de esa categoria (su nombre)
                String clave = json.keySet().toString();
                String resultado = clearString(clave);

                if (resultado.equals(categoria)) {

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
                        for (int j = 0; j < 4; j++) {
                            if (j < items.size()) {
                                JSONObject item = (JSONObject) items.get(j);
                                retorno.add(createItem(item));
                            }
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

    public Item getItemById(String id) {

        Item retorno = null;

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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
        setDateOfBuying(ff);
        JSONObject parsedItem = parseItem(ff);
        int homeIndex = 0;

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

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
            stock = stock + ff.getCantidad();

            // agarro los items que estaban antes
            JSONArray itemList = (JSONArray) jsonCatKey.get("items");

            // creo la categoria auxiliar
            JSONObject category_aux = new JSONObject();
            JSONObject categoryData = new JSONObject();

            // agrego el stock anterior al aux
            categoryData.put("stock", stock);

            int alerta = CasterObjToInt(jsonCatKey.get("alertar"));
            categoryData.put("alertar", alerta);

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
            try (FileWriter file = new FileWriter(path)) {
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

    public void createCategory(Category categoria) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonObject es todo el archivo json
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            JSONObject obj = new JSONObject();
            JSONObject list = new JSONObject();

            list.put("stock", 0);

            list.put("alertar", categoria.getAlerta());

            JSONArray array = new JSONArray();

            list.put("items", array);

            obj.put(categoria.getCategoria(), list);

            // añado esa categoria en el final
            jsonArray.add(obj);

            try (FileWriter file = new FileWriter(path)) {
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
        details.put("cantidad", ff.getCantidad());
        details.put("fecha_compra", ff.getFechaCompra());
        details.put("fecha_venta", ff.getFechaVenta());

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
        int cantidad = CasterObjToInt(info.get("cantidad"));

        String fecha_compra = (String) info.get("fecha_compra");
        String fecha_venta = (String) info.get("fecha_venta");

        Item item = new Item(nombre, descripcion, precioVenta, precioCompra, cantidad);
        item.setId(id);
        item.setFechaCompra(fecha_compra);
        item.setFechaVenta(fecha_venta);

        return item;
    }

    public Category parseCategory(JSONObject ff) {

        String clave = ff.keySet().toString();
        String resultado = clearString(clave);

        JSONObject jsonCategory = (JSONObject) ff.get(resultado);

        int stock = CasterObjToInt(jsonCategory.get("stock"));
        int alerta = CasterObjToInt(jsonCategory.get("alertar"));

        return new Category(resultado, stock, alerta);
    }

    public ArrayList<Category> getAllOfCategory() {
        JSONParser parser = new JSONParser();

        ArrayList stockList = new ArrayList<Category>();

        try (Reader reader = new FileReader(path)) {

            // cositas para pasarlo a json object
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (int i = 0; i < jsonArray.size(); i++) {

                // agarra ese lugarcito donde está i y lo transforma en objeto
                JSONObject json = (JSONObject) jsonArray.get(i);

                Category aux = parseCategory(json);

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
        if (identificators.contains(resultado) || logIdentificators.contains(resultado)) {
            while (identificators.contains(resultado) || logIdentificators.contains(resultado)) {
                int aux = new Random().nextInt(999999);
                resultado = "" + aux;
            }
        }

        return resultado;
    }

    public void setDateOfBuying(Item ff) {
        // nota del editor: dia, mes, año
        String pattern = "dd-MM-yyyy";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());
        ff.setFechaCompra(dateInString);
    }

    public void setLogIdentificators(ArrayList<String> ids) {

    }
}
