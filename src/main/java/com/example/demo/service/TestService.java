package com.example.demo.service;

import com.example.demo.dto.Person;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import oracle.jdbc.OracleTypes;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jdbc.support.oracle.SqlReturnStruct;
import org.springframework.data.jdbc.support.oracle.SqlReturnStructArray;
import org.springframework.data.jdbc.support.oracle.StructMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final DataSource dataSource;


    private SimpleJdbcCall getTestCrs(DataSource dataSource) {
        var call = new SimpleJdbcCall(dataSource);
        call.withFunctionName("test_crs");
        call.withoutProcedureColumnMetaDataAccess();
        call.declareParameters(new SqlOutParameter("result", OracleTypes.CURSOR));
        return call;
    }

//    public List<Person> getAll() {
//        var empCrs = getTestCrs(dataSource);
//        Map<String, Object> out = empCrs.execute();
//        var resultMap = out.get("result");
//        return getPersons((List<Map<String, Object>>) resultMap);
//    }

    @SneakyThrows
    public List<Person> getAll() {
        var call = getAllCall();
        Map<String, Object> out = call.execute();
        var response = (Object[]) out.get("PERSON_LIST");
        return Arrays.stream(response).map(obj -> (Person) obj).collect(Collectors.toList());
    }


    public List<Person> getAllBy() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select * from employee", new PersonResultSetExtractor());
    }


    private List<Person> getPersons(List<Map<String, Object>> list) {
        return list.stream()
                .map(map -> {
                    var person = new Person();
                    person.setId((BigDecimal) map.get("ID"));
                    person.setLastName((String) map.get("LAST_NAME"));
                    person.setFirstName((String) map.get("FIRST_NAME"));
                    return person;
                })
                .collect(Collectors.toList());
    }

    public Person getById(Long id) {
        var call = getEmpAsObject(dataSource);
        Map<String, Object> out = call.execute(id);
        return (Person) out.get("RESULT");
    }


    private SimpleJdbcCall getAllCall() {
        var call = new SimpleJdbcCall(dataSource);
        call.withProcedureName("GET_ALL_PERSON")
                .declareParameters(
                        new SqlOutParameter("PERSON_LIST", OracleTypes.ARRAY,
                                "PERSON_PKG.PERSON_ARRAY",
                                new SqlReturnStructArray<>(new PersonStructMapper())));
        return call;
    }

    class PersonRowMapper implements RowMapper<List<Person>> {

        @Override
        public List<Person> mapRow(ResultSet rs, int rowNum) throws SQLException {
            var list = new ArrayList<Person>();
            while (rs.next()) {
                var person = new Person();
                person.setId(rs.getBigDecimal("id"));
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                list.add(person);
            }
            return list;
        }
    }


    public SimpleJdbcCall getEmpAsObject(DataSource dataSource) {
        return new SimpleJdbcCall(dataSource)
                .withFunctionName("GET_PERSON_OBJ")
                .declareParameters(
                        new SqlParameter("P_ID", OracleTypes.NUMBER),
                        new SqlOutParameter("RESULT", OracleTypes.STRUCT,
                                "PERSON_OBJ", new SqlReturnStruct(Person.class)));
    }


    class PersonResultSetExtractor implements ResultSetExtractor<List<Person>> {

        @Override
        public List<Person> extractData(ResultSet rs) throws SQLException, DataAccessException {
            var list = new ArrayList<Person>();
            while (rs.next()) {
                var person = new Person();
                person.setId(rs.getBigDecimal("id"));
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                list.add(person);
            }
            return list;
        }
    }

    class PersonStructMapper implements StructMapper<Person> {

        @Override
        public Struct toStruct(Person source, Connection conn, String typeName) throws SQLException {
            return conn.createStruct(
                    typeName,
                    new Object[]{
                            source.getId(),
                            source.getFirstName(),
                            source.getLastName()
                    });
        }

        @Override
        public Person fromStruct(Struct struct) throws SQLException {
            Object[] person = struct.getAttributes();
            return Person.builder()
                    .id((BigDecimal) person[0])
                    .firstName(String.valueOf(person[1]))
                    .lastName(String.valueOf(person[2]))
                    .build();
        }

    }

}
