package com.auction.app.domains.tag;

import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagResponse toResponse(Tag tag){
        TagResponse tagResponse = new TagResponse();

        tagResponse.setTagName(tag.getTagName());
        tagResponse.setTagId(tagResponse.getTagId());

        return tagResponse;
    }
}
