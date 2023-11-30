package entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Table(value = "creditsamplewindow")
data class CreditsamplewindowEntity (
    @Column(value = "CID")
    var cid: String? = null,
    @Column(value = "STAGE_BEF")
    var stageBef: String? = null,
    @Column(value = "STAGE_AFT")
    var stageAft: String? = null,
    @Column(value = "START_DATE")
    var startDate: String? = null,
    @Column(value = "CLOSE_DATE")
    var closeDate: String? = null,
)