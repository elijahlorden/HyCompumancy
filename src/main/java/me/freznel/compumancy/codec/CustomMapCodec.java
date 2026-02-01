package me.freznel.compumancy.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomMapCodec<T> implements Codec<T> {

    private final Class<T> cls;
    private final String keyField;
    private final Function<T, String> encodeFunc;
    private final Function<String, T> decodeFunc;

    public CustomMapCodec(Class<T> cls, String keyField, Function<T, String> encodeFunc, Function<String, T> decodeFunc) {
        this.cls = cls;
        this.keyField = keyField;
        this.encodeFunc = encodeFunc;
        this.decodeFunc = decodeFunc;
    }


    @Override
    public @Nullable T decode(BsonValue bsonValue, ExtraInfo extraInfo) {
        BsonDocument doc = bsonValue.asDocument();
        String key = Codec.STRING.decode(doc.get(keyField), extraInfo);
        return decodeFunc.apply(key);
    }

    @Override
    public BsonValue encode(T t, ExtraInfo extraInfo) {
        String key = encodeFunc.apply(t);
        BsonDocument doc = new BsonDocument();
        doc.put(keyField, Codec.STRING.encode(key, extraInfo));
        return doc;
    }

    @Override
    public @NonNull Schema toSchema(@NonNull SchemaContext schemaContext) {
        ObjectSchema schema = new ObjectSchema();
        schema.setTitle("CustomMapCodec: " + cls.getName());
        schema.setAdditionalProperties(false);
        Map<String, Schema> props = new HashMap<>();
        props.put(keyField, schemaContext.refDefinition(Codec.STRING));
        schema.setProperties(props);
        return schema;
    }
}
