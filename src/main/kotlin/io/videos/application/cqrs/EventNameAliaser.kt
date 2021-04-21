package io.videos.application.cqrs

import org.axonframework.serialization.Serializer
import org.axonframework.serialization.xml.XStreamSerializer
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AssignableTypeFilter

@Configuration
class EventNameAliaser {

    @Bean
    fun eventSerializer(): Serializer =
        XStreamSerializer
            .builder()
            .build()
            .registerAliases()

    private fun XStreamSerializer.registerAliases(): XStreamSerializer {
        eventDefinitions()
            .map { Class.forName(it.beanClassName) }
            .forEach { registerEventAlias(it) }
        return this
    }

    private fun XStreamSerializer.registerEventAlias(eventClass: Class<*>) {
        try {
            val annotation = eventClass.getAnnotation(EventName::class.java)
            this.addAlias(annotation.name, eventClass)
        } catch (e: NullPointerException) {
            throw MissingEventNameAnnotationException(eventClass.name)
        }
    }

    private fun eventDefinitions(): Set<BeanDefinition> {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AssignableTypeFilter(Event::class.java))
        return provider.findCandidateComponents("io.videos.application")
    }
}

class MissingEventNameAnnotationException(className: String) : RuntimeException("Missing @EventName for $className")