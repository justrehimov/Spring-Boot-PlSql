package com.example.demo;

import com.example.demo.service.TestService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication implements CommandLineRunner {

    private final DataSource dataSource;
    private final TestService testService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {




//
//        var empIdsCall = getEmpIds(dataSource);
//
//        Map<String, Object> out = empIdsCall.execute();
//        out.entrySet().stream()
//                .forEach(stringObjectEntry -> {
//                    System.out.println(stringObjectEntry.getKey() + " : " + getEmpIds((ARRAY)stringObjectEntry.getValue()));
//                });


//        var empCall = getEmpCall(dataSource);
//
//        Map<String, Object> out = empCall.execute();
//        out.entrySet().stream()
//                .forEach(stringObjectEntry -> {
//                    System.out.println(stringObjectEntry.getKey() + " : " + stringObjectEntry.getValue());
//                });

//        var empCrs = getTestCrs(dataSource);
//
//        Map<String, Object> out = empCrs.execute();
//        out.entrySet().stream()
//                .forEach(stringObjectEntry -> {
//                    System.out.println(stringObjectEntry.getKey() + " : "
//                            + getPersons((List<Map<String, Object>>)stringObjectEntry.getValue()));
//                });

    }



    @SneakyThrows
    private List<String> getNames(ARRAY array) {
        String[] arr = (String[])array.getArray();
        return Arrays.stream(arr).collect(Collectors.toList());
    }

    @SneakyThrows
    private List<BigDecimal> getEmpIds(ARRAY array) {
        BigDecimal[] arr = (BigDecimal[])array.getArray();
        return Arrays.stream(arr).collect(Collectors.toList());
    }

    private SimpleJdbcCall getEmpNames(JdbcTemplate jdbcTemplate) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
        simpleJdbcCall.setCatalogName("TEST_PKG");
        simpleJdbcCall.withProcedureName("GET_EMP_NAMES");
        simpleJdbcCall.declareParameters(
                new SqlParameter("FIRST_NAME", OracleTypes.VARCHAR),
                new SqlOutParameter("FIRST_NAME_LIST", OracleTypes.ARRAY, "TEST_PKG.STRING_LIST"));
        return simpleJdbcCall;
    }

    private SimpleJdbcCall getEmpIds(DataSource dataSource) {
        var call = new SimpleJdbcCall(dataSource);
        call.withCatalogName("DEMO_PKG");
        call.withFunctionName("GET_EMP_IDS");
        call.declareParameters(
                new SqlOutParameter("RETURN", OracleTypes.ARRAY, "DEMO_PKG.INTEGER_LIST"));
        return call;
    }

    private SimpleJdbcCall getEmpCall(DataSource dataSource) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
        simpleJdbcCall.withCatalogName("OBJ_PKG");
        simpleJdbcCall.withFunctionName("GET_PERSON");
        simpleJdbcCall.returningResultSet("return", new BeanPropertyRowMapper<>(Person.class));
        return simpleJdbcCall;
    }

    @Data
    class Person {

    }




}
