package by.maryana.utils;

import org.hashids.Hashids;

public class CryptoTool {

    private final Hashids hashids;

    public CryptoTool(String salt) {
        int minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    public String encode(Long value){
        return hashids.encode(value);
    }

    public Long decode(String hashValue){
        long[] res =  hashids.decode(hashValue);
        if(res != null && res.length > 0){
            return res[0];
        }
        return null;
    }
}
