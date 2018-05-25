import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class StudentDeserializer implements JsonDeserializer<Student> {

    @Override
    public Student deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonElement bordingPassElement = json.getAsJsonObject().get("book");
        if (bordingPassElement.isJsonNull()) {
            return new BoardingPassResponse((BoardingPass) context.deserialize(bordingPassElement.getAsJsonNull(), BoardingPass.class));
        } else if (bordingPassElement.isJsonArray()) {
            return new BoardingPassResponse((BoardingPass[]) context.deserialize(bordingPassElement.getAsJsonArray(), BoardingPass[].class));
        } else if (bordingPassElement.isJsonObject()) {
            return new BoardingPassResponse((BoardingPass) context.deserialize(bordingPassElement.getAsJsonObject(), BoardingPass.class));
        } else {
            throw new JsonParseException("Unsupported type of bordingPass element");
        }
    }
}