
export interface Post {
  id: number;
  user: string;
  content: string;
  createdAt: Date;
  threadId: number;
  likes: number;
  isLiked: boolean;
}
