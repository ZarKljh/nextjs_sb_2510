/** localhost:3000 서버에서는 DB의 값을 가져올수 없다.*/
/** 그러므로 백엔드에게 값을 받기 위해서 요청을 보내야한다*/
/** 백엔드 서버인 localhost:8080 에게 요청을 보내는 명령어가 await fetch 이다*/
/** fetch는 ()안에있는 서버에게 요청을 보내는 명령어이다*/
/** await는 서버로부터 응답이 돌아올때까지 기다리는 명령어이다*/
"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

export default function Article() {
  const [articles, setArticles] = useState([]);

  useEffect(() => {
    //getData();
    fetchArticles();
  }, []);
  /**
  const getData = async () => {
    const result = await fetch("http://localhost:8090/api/v1/articles").then(
      (row) => row.json()
    );
    setArticles(result.data.articles);
    console.log(result.data.articles);
  };
  */
  /*
  const fetchArticles = async () => {
    await fetch("http://localhost:8090/api/v1/articles")
      .then((result) => result.json())
      .then((result) => setArticles(result.data.articles))
      .catch((err) => console.error(err));
  };
  */
  const fetchArticles = async () => {
    await fetch("http://localhost:8090/api/v1/articles")
      .then((result) => result.json())
      .then((result) => setArticles(result.data.articles))
      .catch((err) => console.error(err));
  };

  const handleDelete = async (id) => {
    //id 변수를 포함하려면 백틱`을 사용해야한다
    const response = await fetch(
      `http://localhost:8090/api/v1/articles/${id}`,
      {
        //method는 대문자로 쓰는 것이 관례이다
        method: "DELETE",
      }
    );

    if (response.ok) {
      alert("delete complete");
      //새로고침을 하는 코드
      fetchArticles();
    } else {
      alert("fail");
    }
  };

  return (
    <>
      <ArticleForm fetchArticles={fetchArticles}></ArticleForm>
      <h4>번호 / 제목 / 생성일</h4>
      {articles.length == 0 ? (
        <p>현재 게시물이 없습니다.</p>
      ) : (
        <ul>
          {articles.map((article) => (
            <li key={article.id}>
              {article.id}/
              <Link href={"/articles/${article.id}"}>{article.subject}</Link> /
              {article.createdDate}
              <button onClick={() => handleDelete(article.id)}>삭제</button>
            </li>
          ))}
        </ul>
      )}
    </>
  );
}

function ArticleForm({ fetchArticles }) {
  const [article, setArticle] = useState({ subject: "", content: "" });

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:8090/api/v1/articles", {
      method: "POST",
      //서버에게 주고받는 데이터를 json형태로 하겠다고 선언하는 것
      headers: {
        "Content-Type": "application/json",
      },
      //무엇을 json으로 할지 선언한것
      body: JSON.stringify(article),
    });

    if (response.ok) {
      alert("success");
      //새로고침을 하는 코드
      fetchArticles();
      setArticle({ subject: "", content: "" });
    } else {
      alert("fail");
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
      <h4>게시물 작성</h4>
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
        <input type="submit" value="등록"></input>
        {/* <button type="submit">등록</button> */}
      </form>
    </>
  );
}
