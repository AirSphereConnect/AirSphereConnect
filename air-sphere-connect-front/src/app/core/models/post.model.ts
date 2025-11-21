
export interface Post {
  id: number;
  content: string;
  userId: number;
  username: string;
  userRole: string;
  threadId: number;
  threadTitle: string;
  threadOwnerId?: number;
  createdAt: Date | string;
  updatedAt?: Date | string;
  likeCount: number;
  dislikeCount?: number;
  currentUserReaction?: 'LIKE' | 'DISLIKE' | null;
  isFlagged?: boolean;
}
