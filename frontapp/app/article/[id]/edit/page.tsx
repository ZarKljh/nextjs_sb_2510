"use client";

import api from "@/app/utils/api";
import { useParams, useRouter } from "next/navigation";
import { useState, useEffect } from "react";

export default function ArticleEdit() {
  const params = useParams();
  const router = useRouter();
  const [article, setArticle] = useState({ subject: "", content: "" });

  //userEffect 는 페이지가 실행될때 맨처음 랜더링을 한다는 뜻
  // 마지막 [] 는 1번만 실행한다는 뜻이다.
  useEffect(() => {
    api
      .get("/members/me")
      .then((res) => console.log(res))
      .catch((err) => {
        console.log(err);
        alert("로그인 후 이용해주세요");
        router.push("/member/login");
      });
    fetchArticle();
  }, []);

  //useEffect에 의해 랜더링 되는 메소드이다
  const fetchArticle = async () => {
    api
      .get(`/articles/${params.id}`)
      .then((response) => setArticle(response.data.data.article))
      .catch((err) => console.log(err));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    await api
      .patch(`/articles/${params.id}`, article)
      .then(function (res) {
        alert("seccess");.
        router.push(`/article/${params.id}`);
      })
      .catch(function (err) {
        alert("fail");
      });
  };

  //입력창에 값을 입력할 때마다 동작한다.
  // e 는 이벤트 객체이다.
  const handleChange = (e) => {
    const { name, value } = e.target;
    setArticle({ ...article, [name]: value });
    //console.log({...article, [name]: value});
  };

  return (
    <>
      <h4>게시물 수정</h4>
      <form onSubmit={handleSubmit}>
        <label>
          제목:
          <input
            type="text"
            name="subject"
            onChange={handleChange}
            value={article.subject}
          />
        </label>
        <br></br>
        <label>
          내용:
          <input
            type="text"
            name="content"
            onChange={handleChange}
            value={article.content}
          />
        </label>
        <input type="submit" value="수정"></input>
        {/* <button type="submit">등록</button> */}
      </form>
    </>
  );
}
