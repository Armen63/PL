package com.example.armen.pl.util;


public class Constant {

    public class API {
        public static final String HOST = "https://tigransarkisian.github.io";
        public static final String PRODUCT_LIST = HOST + "/aca_pl/products.json";
        public static final String PRODUCT_ITEM = HOST + "/aca_pl/products/"; // + id
        public static final String PRODUCT_ITEM_POSTFIX = "/details.json";
        public static final String PRODUCT_ITEM_DEFAULT_IMAGE = "https://s3-eu-west-1.amazonaws.com/developer-application-test/images/3.jpg";
    }

    public class Argument {
        public static final String ARGUMENT_DATA = "ARGUMENT_DATA";
    }

    public class Extra {
        public static final String PRODUCT_ID = "PRODUCT_ID";
        public static final String URL = "URL";
        public static final String POST_ENTITY = "POST_ENTITY";
        public static final String REQUEST_TYPE = "REQUEST_TYPE";
        public static final String NOTIFICATION_DATA = "NOTIFICATION_DATA";
    }

    public class Symbol {
        public static final String SPACE = " ";
    }

    public class Util {
        public static final String UTF_8 = "UTF-8";
    }

    public class POJO {
        public static final String PRODUCT_ID = "product_id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String IMAGE = "image";
        public static final String DESCRIPTION = "description";

        public static final String PRODUCTS = "products";
    }

    public class RequestType {
        public static final int PRODUCT_LIST = 1;
        public static final int PRODUCT_ITEM = 2;
    }

    public class RequestMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String PUT = "PUT";
    }
}
