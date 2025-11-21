export enum PostReportReason {
  SPAM = 'SPAM',
  INAPPROPRIATE_CONTENT = 'INAPPROPRIATE_CONTENT',
  HARASSMENT = 'HARASSMENT',
  FALSE_INFORMATION = 'FALSE_INFORMATION',
  OFF_TOPIC = 'OFF_TOPIC',
  COPYRIGHT_VIOLATION = 'COPYRIGHT_VIOLATION',
  OTHER = 'OTHER'
}

export interface PostReport {
  id: number;
  postId: number;
  reason: PostReportReason;
  description: string;
  status: 'PENDING' | 'REVIEWED' | 'RESOLVED';
  createdAt: string;
  updatedAt: string;
  deletedAt: string;
  userId: number;
}

export interface PostReportRequest {
  postId: number;
  reason: PostReportReason;
  description: string;
}
