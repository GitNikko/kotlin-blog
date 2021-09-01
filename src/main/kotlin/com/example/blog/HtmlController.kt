package com.example.blog

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@Controller
class HtmlController(private val articleRepository: ArticleRepository, private val userRepository: UserRepository) {

    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = "All Articles"
        model["articles"] = articleRepository.findAllByOrderByAddedAtDesc().map { it.render() }
        return "blog"
    }

    @GetMapping("/article/{slug}")
    fun article(@PathVariable slug: String, model: Model): String {
        val article = articleRepository
            .findBySlug(slug)
            ?.render()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This article does not exist")
        model["title"] = article.title
        model["article"] = article
        return "article"
    }

    @PostMapping("/article")
    fun articleSubmit(
        @RequestParam title: String,
        @RequestParam content: String,
        @RequestParam headline: String,
        model: Model): String? {
        model.addAttribute("title", title)
        println("*** Form submitted ***")
        val user = userRepository.findByLogin("smaldini")
        val article = articleRepository.save(Article(title = title, headline = title, content = content, author = user!!))
        return "redirect:/article/${article.slug}"
    }

    fun Article.render() = RenderedArticle(
        slug,
        title,
        headline,
        content,
        author,
        addedAt.format()
    )

    data class RenderedArticle(
        val slug: String,
        val title: String,
        val headline: String,
        val content: String,
        val author: User,
        val addedAt: String)

}