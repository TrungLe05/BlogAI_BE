CREATE TABLE public.blog_likes (
                                   blog_id     UUID      NOT NULL,
                                   user_id     UUID      NOT NULL,
                                   created_at  TIMESTAMP NOT NULL DEFAULT now(),
                                   CONSTRAINT pk_blog_likes PRIMARY KEY (blog_id, user_id),
                                   CONSTRAINT fk_blog_likes_blog FOREIGN KEY (blog_id) REFERENCES public.blogs(id)  ON DELETE CASCADE,
                                   CONSTRAINT fk_blog_likes_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);
CREATE INDEX idx_blog_likes_user ON public.blog_likes(user_id);
