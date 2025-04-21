package studio.clashbuddy.clashaccess.security;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomGenerator {

        public static String generateNumber(int length){
            String NUMBER = "0123456789";
            return RandomStringUtils.random(length, NUMBER);
        }

        public static String generateText(int length){
            String NUMBER = "A0Bcdllkjfsjksduiferiorweijoiwrety8wrbyusgdfsajhbahd9wqe981y23yibsadhaslsaodhasobdasbdvasucvknacq9wheqwmldiashGYGuyeqvytrfrewqdasvy8asg8IGYGRgebwfie12345KOWNshbdsadb6fhujr7yfdsfO8dsabhbsa9";
            return RandomStringUtils.random(length, NUMBER);
        }


}
