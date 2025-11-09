package gitlet;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static gitlet.Commands.merge;
import static gitlet.Utils.*;
import static java.io.File.separator;

/**
 * The class processes remote commands.
 * 该类拥有一系列实现远程命令的操作，都建立在远程仓库有与本地仓库相同目录结构的基础上。
 * @author ChenJinzhao
 */
class RemoteCommands {
    /**
     * Add a new remote to local repository.
     * 将一个新的远程仓库加入本地记录，由于这是模拟远程
     * 所以传入的路径仍是本地路径，需要包含.gitlet
     */
    static void addRemote(String remoteName, String path) {
        File target = join(Repository.REMOTE_DIR, remoteName);
        if (target.exists()) {
            Main.printError(ErrorMessage.REMOTE_ALREADY_EXISTING.getMessage());
        }
        StringBuilder resultPath = new StringBuilder();
        for (int i = 0; i < path.length(); i += 1) {
            char temp = path.charAt(i);
            if (temp == '/') {
                resultPath.append(separator);
            } else {
                resultPath.append(temp);
            }
        }
        writeContents(target, resultPath.toString());
    }

    /**
     * Remove a remote from the local repository.
     * 将一个远程从本地记录移除。
     */
    static void removeRemote(String remoteName) {
        File target = join(Repository.REMOTE_DIR, remoteName);
        if (!target.exists()) {
            Main.printError(ErrorMessage.REMOTE_NON_EXISTING.getMessage());
        }
        target.delete();
    }

    /**
     * Add all commits from current branch head to the same commit
     * as the remote branch head to the front of remote branch.
     * 根据给定的远程分支头提交，从本地当前头提交回溯查找历史记录是否有该给定提交，
     * 若未找到则推送失败，否则把从查找位置到当前头提交的所有提交（和文件）全部复制到远程分支头
     * 并更新远程分支头。
     */
    static void push(String remoteName, String remoteBranchName) {
        RemoteRepository remoteRepository =
                new RemoteRepository(readContentsAsString(join(Repository.REMOTE_DIR, remoteName)));
        if (!join(remoteRepository.BRANCHES_DIR, remoteBranchName).exists()) {
            writeContents(join(remoteRepository.BRANCHES_DIR, remoteBranchName),
                    remoteRepository.getCurrCommit().getId());
            return;
        }
        Commit splitCommit = remoteRepository.getCommitByBranch(remoteBranchName);
        String splitID = splitCommit.getId();
        Commit currCommit = Repository.getCurrCommit();
        Stack<Commit> stack = new Stack<>();
        boolean found = false;
        while (currCommit != null) {
            if (currCommit.getId().equals(splitID)) {
                found = true;
                break;
            }
            stack.push(currCommit);
            currCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                    currCommit.getParent1(), Commit.class);
        }
        if (!found) {
            Main.printError(ErrorMessage.REMOTE_HEAD_NOT_IN_CURR.getMessage());
        }
        while (!stack.empty()) {
            Commit topCommit = stack.peek();
            for (String blobID : topCommit.getBlobs().values()) {
                Blob blobToBeAdded = Repository.getObjectByID(Repository.BLOBS_DIR, blobID,
                        Blob.class);
                Repository.saveObject(remoteRepository.BLOBS_DIR, blobID, blobToBeAdded);
            }
            Repository.saveObject(remoteRepository.COMMITS_DIR, topCommit.getId(), topCommit);
            stack.pop();
        }
        remoteRepository.switchAddBranch(remoteBranchName, Repository.getCurrCommit().getId());
    }


    /**
     * Get all commits from the remote branch head to initial commit to
     * local repository, and overwrite or add remote branch to local.
     * 从远程分支头开始直到最初提交，获取所有本地不存在的提交（和文件）加入本地，
     * 并创建新本地名为【远程名】/【远程分支】的分支，该操作与push是类似的，只是方向相反。
     */
    static void fetch(String remoteName, String remoteBranchName) {
        RemoteRepository remoteRepository =
                new RemoteRepository(readContentsAsString(join(Repository.REMOTE_DIR, remoteName)));
        if (!join(remoteRepository.BRANCHES_DIR, remoteBranchName).exists()) {
            Main.printError(ErrorMessage.REMOTE_BRANCH_NOT_EXISTING.getMessage());
        }
        Commit currRemoteCommit = remoteRepository.getCommitByBranch(remoteBranchName);
        Queue<Commit> queue = new LinkedList<>();
        queue.add(currRemoteCommit);
        while (!queue.isEmpty()) {
            /* 当前提交和所有文件保存 */
            Commit frontCommit = queue.poll();
            String frontCommitID = frontCommit.getId();
            Repository.saveObject(Repository.COMMITS_DIR, frontCommitID, frontCommit);
            for (String blobID : frontCommit.getBlobs().values()) {
                Blob newBlob = Repository.getObjectByID(remoteRepository.BLOBS_DIR, blobID,
                        Blob.class);
                Repository.saveObject(Repository.BLOBS_DIR, blobID, newBlob);
            }
            /* 父提交入队 */
            for (String parentID : frontCommit.getParents()) {
                if (parentID == null) {
                    continue;
                }
                queue.add(Repository.getObjectByID(remoteRepository.COMMITS_DIR, parentID,
                        Commit.class));
            }
        }
        /* 创建新分支 */
        File newBranchDic = join(Repository.BRANCHES_DIR, remoteName);
        newBranchDic.mkdir();
        writeContents(join(newBranchDic, remoteBranchName), currRemoteCommit.getId());
    }

    /**
     * A mixture of one fetch and one merge of the fetched branch and
     * current branch.
     * fetch和merge命令的结合。先用fetch获取提交和文件，以及一个新分支
     * 再将这个新分支合并到当前本地分支。
     */
    static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        merge(remoteName + "/" + remoteBranchName);
    }
}
