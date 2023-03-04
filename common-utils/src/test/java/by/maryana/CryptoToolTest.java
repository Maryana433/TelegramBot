package by.maryana;

import by.maryana.utils.CryptoTool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptoToolTest {

    private static String salt;
    private static CryptoTool cryptoTool;

    @BeforeAll
    public static void init(){
        salt = "testSalt";
        cryptoTool = new CryptoTool(salt);
    }

    @ParameterizedTest
    @ValueSource(longs = {0,100,1000})
    public void shouldProperlyEncodeDecode(Long id){
            String hash = cryptoTool.encode(id);
            assertEquals(id, cryptoTool.decode(hash));
    }

}
