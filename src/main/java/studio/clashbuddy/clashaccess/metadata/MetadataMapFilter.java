package studio.clashbuddy.clashaccess.metadata;
import org.springframework.util.AntPathMatcher;

import java.util.*;

public class MetadataMapFilter {

    public static Map<String, String[]> filterMetadata(
            Set<String> endpoints,
            Set<String> methods,
            Set<String> removeEndpoints,
            Set<String> removeMethods,AntPathMatcher matcher
    ) {


        Set<String> keepEndpoints = new HashSet<>();
        Set<String> deletedEndpoints = new HashSet<>();
        Set<String> keepMethods = new HashSet<>();
        Set<String> deletedMethods = new HashSet<>();

        for(String endpoint : endpoints){
            if(matchesAnyPattern(endpoint,removeEndpoints,matcher)){
                if(methods.size() == 1){
                    var method = methods.stream().findAny().get();
                    if(!removeMethods.contains(method)){
                        keepMethods.add(method);
                        keepEndpoints.add(endpoint);
                    }else{
                        deletedMethods.add(method);
                        deletedEndpoints.add(endpoint);
                    }
                }
                else if(methods.size() > 1){
                    for(var method : methods){
                        if(removeMethods.contains(method))
                            deletedMethods.add(method);
                        else{
                            keepMethods.add(method);
                            keepEndpoints.add(endpoint);
                        }
                    }
                    if(keepMethods.isEmpty())
                        deletedEndpoints.add(endpoint);
                }
            }
            else{
                keepEndpoints.add(endpoint);
                keepMethods.addAll(methods);
            }
        }

        for(var keptMethod : keepMethods){
            if(deletedMethods.contains(keptMethod))
                deletedMethods.remove(keptMethod);
        }

        for(String endpoint: endpoints){
            if(!keepEndpoints.contains(endpoint))
                deletedEndpoints.add(endpoint);
        }



        Map<String, String[]> result = new HashMap<>();
        result.put("keepEndpoints", keepEndpoints.toArray(new String[0]));
        result.put("keepMethods", keepMethods.toArray(new String[0]));
        result.put("deletedEndpoints", deletedEndpoints.toArray(new String[0]));
        result.put("deletedMethods", deletedMethods.toArray(new String[0]));
        return result;
    }

    private static boolean matchesAnyPattern(String path, Set<String> patterns, AntPathMatcher matcher) {
        for (String pattern : patterns) {
            if (matcher.match(pattern, path)) return true;
        }
        return false;
    }
}
