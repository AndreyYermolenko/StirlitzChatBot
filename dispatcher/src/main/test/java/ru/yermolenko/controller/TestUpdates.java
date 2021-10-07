package ru.yermolenko.controller;

public class TestUpdates {
    public static final String MESSAGE_WITH_TEXT = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"type\": \"private\",\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"text\":\"/start\"\n" +
            "}\n" +
            "}";
    public static final String FORWARDED_MESSAGE = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"type\": \"private\",\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"forward_from\": {\n" +
            "     \"last_name\":\"Forward Lastname\",\n" +
            "     \"id\": 222222,\n" +
            "     \"first_name\":\"Forward Firstname\"\n" +
            "  },\n" +
            "  \"forward_date\":1441645550,\n" +
            "  \"text\":\"/start\"\n" +
            "}\n" +
            "}";
    public static final String FORWARDED_CHANNEL_MESSAGE = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"forward_from\": {\n" +
            "     \"id\": -10000000,\n" +
            "     \"type\": \"channel\",\n" +
            "     \"title\": \"Test channel\"\n" +
            "  },\n" +
            "  \"forward_date\":1441645550,\n" +
            "  \"text\":\"/start\"\n" +
            "}\n" +
            "}";
    public static final String MESSAGE_WITH_REPLY = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"text\":\"/start\",\n" +
            "  \"reply_to_message\":{\n" +
            "      \"date\":1441645000,\n" +
            "      \"chat\":{\n" +
            "          \"last_name\":\"Reply Lastname\",\n" +
            "          \"type\": \"private\",\n" +
            "          \"id\":1111112,\n" +
            "          \"first_name\":\"Reply Firstname\",\n" +
            "          \"username\":\"Testusername\"\n" +
            "      },\n" +
            "      \"message_id\":1334,\n" +
            "      \"text\":\"Original\"\n" +
            "  }\n" +
            "}\n" +
            "}";
    public static final String EDITED_MESSAGE = "{\n" +
            "\"update_id\":10000,\n" +
            "\"edited_message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"text\":\"Edited text\",\n" +
            "  \"edit_date\": 1441646600\n" +
            "}\n" +
            "}";
    public static final String MESSAGE_WITH_ENTITIES = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"text\":\"Bold and italics\",\n" +
            "  \"entities\": [\n" +
            "      {\n" +
            "          \"type\": \"italic\",\n" +
            "          \"offset\": 9,\n" +
            "          \"length\": 7\n" +
            "      },\n" +
            "      {\n" +
            "          \"type\": \"bold\",\n" +
            "          \"offset\": 0,\n" +
            "          \"length\": 4\n" +
            "      }\n" +
            "      ]\n" +
            "}\n" +
            "}";
    public static final String MESSAGE_WITH_AUDIO = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"audio\": {\n" +
            "      \"file_id\": \"AwADBAADbXXXXXXXXXXXGBdhD2l6_XX\",\n" +
            "      \"duration\": 243,\n" +
            "      \"mime_type\": \"audio/mpeg\",\n" +
            "      \"file_size\": 3897500,\n" +
            "      \"title\": \"Test music file\"\n" +
            "  }\n" +
            "}\n" +
            "}";
    public static final String VOICE_MESSAGE = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"voice\": {\n" +
            "      \"file_id\": \"AwADBAADbXXXXXXXXXXXGBdhD2l6_XX\",\n" +
            "      \"duration\": 5,\n" +
            "      \"mime_type\": \"audio/ogg\",\n" +
            "      \"file_size\": 23000\n" +
            "  }\n" +
            "}\n" +
            "}";
    public static final String MESSAGE_WITH_DOCUMENT = "{\n" +
            "\"update_id\":10000,\n" +
            "\"message\":{\n" +
            "  \"date\":1441645532,\n" +
            "  \"chat\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"message_id\":1365,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"document\": {\n" +
            "      \"file_id\": \"AwADBAADbXXXXXXXXXXXGBdhD2l6_XX\",\n" +
            "      \"file_name\": \"Testfile.pdf\",\n" +
            "      \"mime_type\": \"application/pdf\",\n" +
            "      \"file_size\": 536392\n" +
            "  }\n" +
            "}\n" +
            "}";
    public static final String INLINE_QUERY = "{\n" +
            "\"update_id\":10000,\n" +
            "\"inline_query\":{\n" +
            "  \"id\": 134567890097,\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"query\": \"inline query\",\n" +
            "  \"offset\": \"\"\n" +
            "}\n" +
            "}";
    public static final String CHOSEN_INLINE_QUERY = "{\n" +
            "\"update_id\":10000,\n" +
            "\"chosen_inline_result\":{\n" +
            "  \"result_id\": \"12\",\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"query\": \"inline query\",\n" +
            "  \"inline_message_id\": \"1234csdbsk4839\"\n" +
            "}\n" +
            "}";
    public static final String CALLBACK_QUERY = "{\n" +
            "\"update_id\":10000,\n" +
            "\"callback_query\":{\n" +
            "  \"id\": \"4382bfdwdsb323b2d9\",\n" +
            "  \"from\":{\n" +
            "     \"last_name\":\"Test Lastname\",\n" +
            "     \"type\": \"private\",\n" +
            "     \"id\":1111111,\n" +
            "     \"first_name\":\"Test Firstname\",\n" +
            "     \"username\":\"Testusername\"\n" +
            "  },\n" +
            "  \"data\": \"Data from button callback\",\n" +
            "  \"inline_message_id\": \"1234csdbsk4839\"\n" +
            "}\n" +
            "}";
    public static final String MESSAGE_WITH_PHOTO = "{\n" +
            "   \"update_id\":354299597,\n" +
            "   \"message\":{\n" +
            "      \"message_id\":254,\n" +
            "      \"from\":{\n" +
            "         \"id\":111111111,\n" +
            "         \"first_name\":\"Test\",\n" +
            "         \"is_bot\":false,\n" +
            "         \"last_name\":\"Testest\",\n" +
            "         \"username\":\"test\",\n" +
            "         \"language_code\":\"en\"\n" +
            "      },\n" +
            "      \"date\":1633612879,\n" +
            "      \"chat\":{\n" +
            "         \"id\":111111111,\n" +
            "         \"type\":\"private\",\n" +
            "         \"first_name\":\"Test\",\n" +
            "         \"last_name\":\"Testest\",\n" +
            "         \"username\":\"test\"\n" +
            "      },\n" +
            "      \"photo\":[\n" +
            "         {\n" +
            "            \"file_id\":\"AgACAgIAAxkBAAP-YV70TprIjRvbHfF4-C7pwh3D2nIAAkK3MRuaavlKEP9YVKoHzMsBAAMCAANzAAMhBA\",\n" +
            "            \"file_unique_id\":\"AQADQrcxG5pq-Up4\",\n" +
            "            \"width\":90,\n" +
            "            \"height\":67,\n" +
            "            \"file_size\":1420\n" +
            "         },\n" +
            "         {\n" +
            "            \"file_id\":\"AgACAgIAAxkBAAP-YV70TprIjRvbHfF4-C7pwh3D2nIAAkK3MRuaavlKEP9YVKoHzMsBAAMCAANtAAMhBA\",\n" +
            "            \"file_unique_id\":\"AQADQrcxG5pq-Upy\",\n" +
            "            \"width\":320,\n" +
            "            \"height\":240,\n" +
            "            \"file_size\":19617\n" +
            "         },\n" +
            "         {\n" +
            "            \"file_id\":\"AgACAgIAAxkBAAP-YV70TprIjRvbHfF4-C7pwh3D2nIAAkK3MRuaavlKEP9YVKoHzMsBAAMCAAN4AAMhBA\",\n" +
            "            \"file_unique_id\":\"AQADQrcxG5pq-Up9\",\n" +
            "            \"width\":350,\n" +
            "            \"height\":263,\n" +
            "            \"file_size\":21322\n" +
            "         }\n" +
            "      ]\n" +
            "   }\n" +
            "}";
    public static final String NAKED_UPDATE = "{\"update_id\":868635992}";
}
