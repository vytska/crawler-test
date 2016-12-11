package vytautas.com.dtos;

import java.util.ArrayList;
import java.util.List;

public class UpdateListRequest {

    private List<String> list = new ArrayList<>();

    public UpdateListRequest() {
    }

    public UpdateListRequest(List<String> list) {
        this.list = list;
    }


    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
