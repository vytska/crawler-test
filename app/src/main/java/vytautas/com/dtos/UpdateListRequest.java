package vytautas.com.dtos;

import java.util.ArrayList;
import java.util.List;

public class UpdateListRequest extends UrlHolder {

    public UpdateListRequest(){}

    public UpdateListRequest(String url){
        setUrl(url);
    }


    private List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
