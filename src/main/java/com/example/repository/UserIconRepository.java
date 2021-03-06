package com.example.repository;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import com.example.domain.UserIcon;

/**
 * ユーザーのアイコンを管理するリポジトリ.
 * 
 * @author iidashuhei
 *
 */
@Repository
public class UserIconRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	// Insert時に自動採番されたIDを取得する
	private SimpleJdbcInsert insert;

	@PostConstruct
	public void init() {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert((JdbcTemplate) template.getJdbcOperations());
		SimpleJdbcInsert withTableName = simpleJdbcInsert.withTableName("user_icons");
		insert = withTableName.usingGeneratedKeyColumns("icon_id");
	}
	
	/**
	 * ユーザーのアイコンを登録する.
	 * 
	 * @param iconImagePath ユーザーのアイコン
	 */
	public UserIcon insert(UserIcon userIcon) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(userIcon);
		Number key = insert.executeAndReturnKey(param);
		userIcon.setIconId(key.intValue());
		return userIcon;
	}
}