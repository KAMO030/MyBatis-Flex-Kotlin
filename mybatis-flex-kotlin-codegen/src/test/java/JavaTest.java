import com.mybatisflex.core.query.QueryWrapper;
import entity.table.DeptTableDef;
import org.junit.jupiter.api.Test;

public class JavaTest {
    int id;

    @Test
    void fn() {
        var wrapper = new QueryWrapper();
        wrapper.select(DeptTableDef.DEFAULT_COLUMNS, DeptTableDef.ALL_COLUMNS);
    }
}
