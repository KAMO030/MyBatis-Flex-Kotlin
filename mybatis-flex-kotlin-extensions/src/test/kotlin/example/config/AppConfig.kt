package com.mybatisflex.kotlin.example.config

import com.mybatisflex.core.mybatis.FlexConfiguration
import com.mybatisflex.spring.FlexSqlSessionFactoryBean
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
@MapperScan("com.mybatisflex.kotlin.example.mapper")
open class AppConfig {


	@Bean
	open fun dataSource(): DataSource? {
		return EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript("schema.sql")
			.addScript("data-kt.sql")
			.build()
	}

	@Bean
	open fun sqlSessionFactory(dataSource: DataSource): SqlSessionFactory? {
		val factoryBean: SqlSessionFactoryBean = FlexSqlSessionFactoryBean()
		factoryBean.setDataSource(dataSource)
		val configuration = FlexConfiguration()
		configuration.logImpl = StdOutImpl::class.java
		factoryBean.setConfiguration(configuration)
		return factoryBean.getObject()
	}

	@EventListener(classes = [ContextStartedEvent::class])
	open fun handleContextStartedEvent() {
		println("handleContextStartedEvent listener invoked!")
	}



}


