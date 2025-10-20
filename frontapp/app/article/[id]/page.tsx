/*다이나믹 라우팅*/
"use client";

import { useParams } from "next/navigation";

export default function ArticleDetail() {
  const params = useParams();

  return <>게시판 상세 {params.id}번</>;
}
