package cn.hutool.db;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Derby数据库单元测试
 *
 * @author looly
 *
 */
public class DerbyTest {

	private static final String DS_GROUP_NAME = "derby";

	//@BeforeAll
	public static void init() throws SQLException {
		Db db = Db.use(DS_GROUP_NAME);
		try{
			db.execute("CREATE TABLE test(a INTEGER, b BIGINT)");
		}catch (SQLException e){
			// 数据库已存在
			return;
		}

		db.insert(Entity.create("test").set("a", 1).set("b", 11));
		db.insert(Entity.create("test").set("a", 2).set("b", 21));
		db.insert(Entity.create("test").set("a", 3).set("b", 31));
		db.insert(Entity.create("test").set("a", 4).set("b", 41));
	}

	@Test
	@Disabled
	public void queryTest() throws SQLException {
		List<Entity> query = Db.use(DS_GROUP_NAME).query("select * from test");
		assertEquals(4, query.size());
	}

	@Test
	@Disabled
	public void findTest() throws SQLException {
		List<Entity> query = Db.use(DS_GROUP_NAME).find(Entity.create("test"));
		assertEquals(4, query.size());
	}
}
