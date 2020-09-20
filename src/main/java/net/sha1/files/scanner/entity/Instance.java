package net.sha1.files.scanner.entity;

public class Instance {
    public Integer id;
    public String name;
    public String path;
    public Long size;

    public Instance(Integer id, String name, String path, Long size) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFullPath() {
        return this.path + "/" + this.name;
    }

}
