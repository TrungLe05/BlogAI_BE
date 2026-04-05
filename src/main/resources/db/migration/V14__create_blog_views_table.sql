CREATE TABLE public.blog_views (
                                   blog_id    UUID      NOT NULL,
                                   user_id    UUID      NOT NULL,
                                   viewed_at  TIMESTAMP NOT NULL DEFAULT now(),
                                   CONSTRAINT pk_blog_views PRIMARY KEY (blog_id, user_id),
                                   CONSTRAINT fk_blog_views_blog FOREIGN KEY (blog_id) REFERENCES public.blogs(id) ON DELETE CASCADE,
                                   CONSTRAINT fk_blog_views_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);
CREATE INDEX idx_blog_views_user ON public.blog_views(user_id);