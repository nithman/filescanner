package net.sha1.files.scanner.controller;

import net.sha1.files.scanner.entity.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
public class ScannerController {
    @Autowired
    JdbcTemplate h2Template;

    @GetMapping("/")
    public String greeting() {
        return "OK";
    }
    String all = "select path,name,size from file order by name,size,path";
    @GetMapping("/all")
    public List<Instance> all() {
        return h2Template.query(
                all,
                (rs, rowNum) ->
                        new Instance(
                                rowNum + 1,
                                rs.getString("name"),
                                toBashPath(rs.getString("path")),
                                rs.getLong("size")
                        )
        );    }

    public static String dual = "select path,name,size from file where name in " +
            "(select name from (SELECT name,count(*) n FROM FILE group by name) where n > 1) " +
            "order by name,path,size";
    @GetMapping("/dual")
    public List<Instance> dual() {
        return dual(h2Template);
    }

    public static List<Instance> dual(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(
                dual,
                (rs, rowNum) ->
                        new Instance(
                                rowNum + 1,
                                rs.getString("name"),
                                toBashPath(rs.getString("path")),
                                rs.getLong("size")
                        )
        );
    }

    public static String plural = "select path,name,size from file where name in " +
            "(select name from (SELECT name,count(*) n FROM FILE group by name,size) where n > 1) " +
            "order by name,path,size";
    @GetMapping("/plural")
    public List<Instance> plural() {
        return plural(h2Template);
    }

    public static List<Instance> plural(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(
                plural,
                (rs, rowNum) ->
                        new Instance(
                                rowNum + 1,
                                rs.getString("name"),
                                toBashPath(rs.getString("path")),
                                rs.getLong("size")
                        )
        );
    }

    String paths = "select path,count(*) kount,sum(size) size from file where name in " +
            "(select name from (SELECT name,count(*) n FROM FILE group by name,size) where n > 1) " +
            "group by path order by path";
    @GetMapping("/path")
    public List<Instance> path() {
        return h2Template.query(
                paths,
                (rs, rowNum) ->
                        new Instance(
                                rowNum + 1,
                                Long.toString(rs.getLong("kount")),
                                toBashPath(rs.getString("path")),
                                rs.getLong("size")
                        )
        );    }

    String single = "select path,name,size from file where name in " +
            "(select name from (SELECT name,count(*) n FROM FILE group by name,size) where n = 1) " +
            "order by path,name,size";
    @GetMapping("/single")
    public List<Instance> single() {
        return h2Template.query(
                single,
                (rs, rowNum) ->
                        new Instance(
                                rowNum + 1,
                                rs.getString("name"),
                                toBashPath(rs.getString("path")),
                                rs.getLong("size")
                        )
        );    }

    private static String toBashPath(String path) {
        return "/" + path.replace(":", "").replace("\\", "/");
    }

    @GetMapping("/duals")
    public ModelAndView displayDuels(Map<String, Object> model) {
        model.put("instances", dual());
        return new ModelAndView("index", model);
    }

    @GetMapping("/plurals")
    public ModelAndView displayInstance(Map<String, Object> model) {
        model.put("instances", plural());
        return new ModelAndView("index", model);
    }

    @GetMapping("/paths")
    public ModelAndView displayPaths(Map<String, Object> model) {
        model.put("instances", path());
        return new ModelAndView("index", model);
    }

    @GetMapping("/singles")
    public ModelAndView displaySingles(Map<String, Object> model) {
        model.put("instances", single());
        return new ModelAndView("index", model);
    }

    @GetMapping("/alls")
    public ModelAndView displayAll(Map<String, Object> model) {
        model.put("instances", all());
        return new ModelAndView("index", model);
    }
}
