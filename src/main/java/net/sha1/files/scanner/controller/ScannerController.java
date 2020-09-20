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

    String plural = "select path,name,size from file where name in " +
            "(select name from (SELECT name,count(*) n FROM FILE group by name,size) where n > 1) " +
            "order by path,name,size";
    @GetMapping("/plural")
    public List<Instance> plural() {
        return h2Template.query(
                plural,
                (rs, rowNum) ->
                        new Instance(
                                rowNum + 1,
                                rs.getString("name"),
                                toBashPath(rs.getString("path")),
                                rs.getLong("size")
                        )
        );    }

    private String toBashPath(String path) {
        return "/" + path.replace(":", "").replace("\\", "/");
    }

    @GetMapping("/plurals")
    public ModelAndView displayInstance(Map<String, Object> model) {
        model.put("instances", plural());
        return new ModelAndView("index", model);
    }

    @GetMapping("/alls")
    public ModelAndView displayAll(Map<String, Object> model) {
        model.put("instances", all());
        return new ModelAndView("index", model);
    }
}
