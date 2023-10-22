import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.mybatisflex.annotation.Table
import internal.config.flex.*
import internal.gen.visitor.MapperVisitor
import internal.gen.visitor.TableDefVisitor
import internal.util.file.flexConfigs

internal class MybatisFlexKSP : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        initConfigs(flexConfigs)
        if (!Enable.value) return emptyList()
        logger.warn("mybatis flex kotlin symbol processor run start...")
        generate(resolver)
        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun generate(resolver: Resolver) {
        val tableDefVisitor = TableDefVisitor()
        val seq = resolver
            .getSymbolsWithAnnotation(Table::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind === ClassKind.CLASS
            }.filter {
                it.validate()
            }

        seq.forEach {
            it.accept(tableDefVisitor, Unit)
        }

        tableDefVisitor.tablesGenerator()

        if (MapperGenerateEnable.value) {
            val mapperVisitor = MapperVisitor(
                resolver.getClassDeclarationByName(MapperBaseClass.value) ?:
                resolver.getClassDeclarationByName(MapperBaseClass.BASE_MAPPER)!!
            )
            seq.filter {
                val table = it.getAnnotationsByType(Table::class).first()
                table.mapperGenerateEnable
            }.forEach {
                it.accept(mapperVisitor, Unit)
            }
        }
    }

    // 用于打印 options 中的内容，仅调试时使用。
    @Suppress("unused")
    private fun logOptions() = options.forEach { (key, value) ->
        logger.warn("options:  $key = $value")
    }
}