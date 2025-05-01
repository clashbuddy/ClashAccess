package studio.clashbuddy.clashaccess.helpers;

import studio.clashbuddy.clashaccess.metadata.ClashScannedEndpointMetadata;

import java.util.Arrays;
import java.util.List;

public class EndpointReadHelper {



    public static List<String> privateEndpoints(ClashScannedEndpointMetadata metadata) {
        return Arrays.stream(metadata.getEndpoints()).filter(e -> !Arrays.asList(metadata.getPublicEndpoints()).contains(e)).toList();
    }


    public static List<String> privateMethods(ClashScannedEndpointMetadata metadata) {
        return Arrays.stream(metadata.getHttpMethods()).filter(e -> !Arrays.asList(metadata.getPublicHttpMethods()).contains(e)).toList();
    }

    private static String formatArray(String[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        } else if (arr.length == 1) {
            return arr[0];
        } else {
            return String.join(",", arr);
        }
    }
}
