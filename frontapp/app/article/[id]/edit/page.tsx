"use client";

import { useParams, useRouter } from "next/navigation";
import { useState, useEffect } from "react";

export default function ArticleEdit() {
  const params = useParams();
  const router = useRouter();
  const [article, setArticle] = useState({ subject: "", content: "" });

  //userEffect 는 페이지가 실행될때 맨처음 랜더링을 한다는 뜻
  // 마지막 [] 는 1번만 실행한다는 뜻이다.
  useEffect(() => {
    fetchArticle();
  }, []);

  //useEffect에 의해 랜더링 되는 메소드이다
  const fetchArticle = async () => {
    await fetch(`http://localhost:8090/api/v1/articles/${params.id}`)
      .then((result) => result.json())
      .then((result) => setArticle(result.data.article))
      .catch((err) => console.error(err));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch(
      `http://localhost:8090/api/v1/articles/${params.id}`,
      {
        method: "PATCH",
        //서버에게 주고받는 데이터를 json형태로 하겠다고 선언하는 것
        headers: {
          "Content-Type": "application/json",
        },
        //무엇을 json으로 할지 선언한것
        body: JSON.stringify(article),
      }
    );

    if (response.ok) {
      alert("update success");
      router.push(`/article/${params.id}`);
    } else {
      alert("udate fail");
    }
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
