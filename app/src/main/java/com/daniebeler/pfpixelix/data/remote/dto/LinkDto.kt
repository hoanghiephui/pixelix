package com.daniebeler.pfpixelix.data.remote.dto

import com.daniebeler.pfpixelix.domain.model.Link

data class LinkDto(
    val href: String, val rel: String
) : DtoInterface<Link> {
    override fun toModel(): Link {
        return Link(
            href = href, rel = rel
        )
    }
}