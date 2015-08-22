package kale.jsonannotation;

import com.google.gson.Gson;

import org.junit.Test;

import test.array.Array;
import test.nest.Nest;
import test.simple.Simple;

import static org.junit.Assert.assertNotNull;

/**
 * @author Jack Tony
 * @date 2015/8/22
 */
public class APIServiceTest {

    @Test
    public void test_simple() {
        Gson gson = new Gson();
        Simple simple = gson.fromJson(APIService.simpleStr, Simple.class);
        assertNotNull(simple);
        assertNotNull(simple.getId());
        assertNotNull(simple.getBody());
        assertNotNull(simple.getNumber());
        assertNotNull(simple.getCreatedAt());
    }

    @Test
    public void test_array() {
        Gson gson = new Gson();
        Array[] array = gson.fromJson(APIService.arrayStr, Array[].class);
        assertNotNull(array[0]);
        assertNotNull(array[0].getId());
        assertNotNull(array[0].getBody());
        assertNotNull(array[0].getNumber());
        assertNotNull(array[0].getCreatedAt());
    }

    @Test
    public void test_nest() {
        Gson gson = new Gson();
        Nest nest = gson.fromJson(APIService.nestStr, Nest.class);
        assertNotNull(nest);
        assertNotNull(nest.getId());
        assertNotNull(nest.getBody());
        assertNotNull(nest.getNumber());
        assertNotNull(nest.getCreatedAt());
        assertNotNull(nest.getChild());
        assertNotNull(nest.getChild().getId());
        assertNotNull(nest.getChild().getName());
    }
    
    
}