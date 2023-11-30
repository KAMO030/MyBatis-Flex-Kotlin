package entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Table(value = "creditfirstuse")
data class CreditfirstuseEntity (
    @Column(value = "CID")
    var cid: String? = null,

    @Column(value = "FST_USE_DT")
    var fstUseDt: String? = null
)