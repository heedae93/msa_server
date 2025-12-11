from ultralytics import YOLO

if __name__ == '__main__':
    # 1. 모델 불러오기 (기존의 똑똑한 yolov8n-cls 모델을 기반으로 시작)
    model = YOLO('yolov8n-cls.pt')

    # 2. 학습 시작 (내 데이터로 재교육)
    # data: 아까 만든 데이터셋 폴더의 '절대 경로'를 입력하세요.
    # epochs: 공부 횟수 (보통 20~50번 정도면 됩니다)
    results = model.train(
        data='./dataset',
        epochs=30,
        imgsz=224,
        project='dog_emotion_project',
        name='dog_emotion_model'
    )