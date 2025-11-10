
export interface Post {
  id: number;
  content: string;
  userId: number;
  username: string;
  threadId: number;
  threadTitle: string;
  createdAt: Date | string;
  updatedAt?: Date | string;
  likeCount: number;
  dislikeCount?: number;
  currentUserReaction?: 'LIKE' | 'DISLIKE' | null;
  isFlagged?: boolean;
}
