package hexlet.code.utils;

public class NamedRoutes {
    //root
    public static String rootPath() {
        return "/";
    }
    //url related
    public static String urlsPath() {
        return "/urls";
    }
    public static String urlPath(String id) {
        return "/urls/" + id;
    }
    public static String urlPath(Long id) {
        return "/urls/" + id;
    }
    public static String checkPath(Long id) {
        return "/urls/" + id + "/checks";
    }
    public static String checkPath(String id) {
        return "/urls/" + id + "/checks";
    }
}
