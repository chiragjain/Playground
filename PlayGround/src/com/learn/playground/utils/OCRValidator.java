
package com.learn.playground.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OCRValidator {

    private int charactersInGroup;
    private int normalGroups;
    private int specialGroups;
    // private int normalGroupMax;
    // private int specialGroupMax;
    private boolean areNumbersGrouped;

    private enum GroupOrder {
        GroupOrderAscending,
        GroupOrderDescending,
        GroupOrderRandom
    }

    /*
     * private enum LotteryType { LotteryTypePowerball, LotteryTypeMegaMillions,
     * LotteryTypePick3, LotteryTypeOther }
     */

    private GroupOrder groupOrder;

    private HashMap<String, String> characterReplacement;

    public OCRValidator() {
        charactersInGroup = 2;
        normalGroups = 5;
        specialGroups = 1;

        // normalGroupMax = 56;
        // specialGroupMax = 35;

        groupOrder = GroupOrder.GroupOrderRandom;
        areNumbersGrouped = false;

        characterReplacement = new HashMap<String, String>();
        characterReplacement.put("B", "6");
        characterReplacement.put("D", "0");
        characterReplacement.put("H", "11");
        characterReplacement.put("I", "1");
        characterReplacement.put("O", "0");
        characterReplacement.put("o", "0");
        characterReplacement.put("A", "4");
        characterReplacement.put("a", "6");
        characterReplacement.put("S", "5");
        characterReplacement.put("s", "5");
        characterReplacement.put("U", "0");

    }

    public ArrayList<ArrayList<String>> validate(String primaryString) {
        if (primaryString != null && primaryString.length() > 0) {
            ArrayList<ArrayList<String>> primary = validateText(primaryString);

            ArrayList<ArrayList<String>> finalList = validateLotterySpecific(primary);

            if (areNumbersGrouped) {
                ArrayList<ArrayList<String>> newFinalList = new ArrayList<ArrayList<String>>();
                for (ArrayList<String> line : finalList) {
                    ArrayList<String> newLine = new ArrayList<String>();
                    if (line.size() == 1) {
                        String group = line.get(line.size() - 1);
                        for (int i = 0; i < group.length(); i++) {
                            newLine.add(group.substring(i, i + 1));
                        }
                    }
                    newFinalList.add(newLine);
                }
                return newFinalList;
            } else {
                return finalList;
            }
        }
        return null;
    }

    private ArrayList<ArrayList<String>> validateLotterySpecific(ArrayList<ArrayList<String>> lines) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (ArrayList<String> line : lines) {
            result.add(validateLineLotterySpecific(line));
        }
        return result;
    }

    private ArrayList<String> validateLineLotterySpecific(ArrayList<String> line) {
        if (line.size() != normalGroups + specialGroups) {
            return line;
        }
        if (groupOrder == GroupOrder.GroupOrderAscending) {
            ArrayList<String> result = new ArrayList<String>();
            for (int i = 0; i < normalGroups; i++) {
                String currentGroup = line.get(i);
                String nextGroup = null;
                String previousGroup = null;
                String resultGroup = currentGroup;

                if ((i + 1) < normalGroups) {
                    nextGroup = line.get(i + 1);
                }

                if ((i - 1) >= 0) {
                    previousGroup = line.get(i - 1);
                }

                if (nextGroup != null && nextGroup.length() > 0) {
                    if (Integer.valueOf(currentGroup) >= Integer.valueOf(nextGroup)) {
                        //Error
                        resultGroup = checkGroupWithGroup(currentGroup, previousGroup, true);
                    }
                }

                currentGroup = resultGroup;
                line.set(i, currentGroup);

                if (previousGroup != null && previousGroup.length() > 0) {
                    if (Integer.valueOf(currentGroup) <= Integer.valueOf(previousGroup)) {
                        //Error
                        resultGroup = checkGroupWithGroup(currentGroup, previousGroup, false);
                    }
                }

                result.add(resultGroup);
            }
            result.add(line.get(line.size() - 1));

            return result;
        }

        return line;

    }

    private String checkGroupWithGroup(String group, String nextGroup, boolean isNext) {
        if (group.length() == 0 || nextGroup.length() == 0)
            return group;
        if (Integer.valueOf(group) == Integer.valueOf(nextGroup)) {
            String lastDigit = group.substring(group.length() - 1);
            String nextLastDigit = nextGroup.substring(nextGroup.length() - 1);
            String newLastDigit = "";
            newLastDigit = compareCharacters(lastDigit, nextLastDigit, isNext);
            String result = group.replaceAll(lastDigit, newLastDigit);
            if (Integer.valueOf(result) == Integer.valueOf(nextGroup)) {
                return "";
            } else {
                return result;
            }
        } else {
            return "";
        }
    }

    private String compareCharacters(String char1, String char2, boolean forwardCheck) {
        if (forwardCheck) {
            if (char1.contentEquals("8") && char2.contains("5")) {
                return "6";
            }
        } else {
            if (char1.contentEquals("5") && char2.contains("8")) {
                return "6";
            }
        }
        return char1;
    }

    private ArrayList<ArrayList<String>> validateText(String primaryString) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        String[] linesArray = primaryString.split("\n");

        List<String> lines = Arrays.asList(linesArray);

        for (String line : lines) {
            if (line.length() > 0) {
                ArrayList<String> groups = validateGroupsInLine(line);
                if (groups != null && groups.size() > 0) {
                    result.add(groups);
                }
            }
        }

        return result;

    }

    private ArrayList<String> validateGroupsInLine(String line) {
        //Separate numbers using spaces and special characters
        String[] groupsArray = line.split(" ");

        List<String> groups = Arrays.asList(groupsArray);
        ArrayList<String> resultGroups = new ArrayList<String>();

        //Remove empty groups
        groups = removeEmptyGroups(groups);

        if (groups.size() < normalGroups + specialGroups) {
            //Try to split into proper number of groups if possible
            groups = splitGroups(groups);
        }

        boolean ignoreAllAlphabetGroups = true;
        if (groups.size() == normalGroups + specialGroups) {
            ignoreAllAlphabetGroups = false;
        }

        int i = 0;
        for (String group : groups) {
            if (i == 0) {
                resultGroups.addAll(validateFirstGroup(group, ignoreAllAlphabetGroups));
            } else {
                resultGroups.addAll(validateCharactersInGroup(group, ignoreAllAlphabetGroups));
            }
            i++;
        }

        return resultGroups;
    }

    private ArrayList<String> validateFirstGroup(String group, boolean ignoreAllAlphabetGroups) {
        ArrayList<String> resultArray = new ArrayList<String>();

        group = removeSpecialCharacters(group);

        if (group.length() <= charactersInGroup && group.length() > 0) {
            // Replace Os, Bs, Hs, etc
            group = replaceAlphabets(group);

            // Check if all characters in group are alphabets
            if (isAllAlphabets(group)) {
                if (!ignoreAllAlphabetGroups) {
                    // Return empty if we should consider all alphabet groups
                    resultArray.add("");
                }
            } else if (isAllNumbers(group)) {
                if (group.length() != charactersInGroup) {
                    // Return empty
                    resultArray.add("");
                } else {
                    resultArray.add(group);
                }
            } else {
                //Return null
            }
        } else if (group.length() > charactersInGroup) {
            //Remove Alphabets
            group = removeAlphabets(group);

            if (isAllNumbers(group)) {
                if (group.length() != charactersInGroup) {
                    // Return empty
                    resultArray.add("");
                } else {
                    resultArray.add(group);
                }
            }
        } else {
            //Return nil, empty group
        }

        return resultArray;
    }

    private ArrayList<String> validateCharactersInGroup(String group, boolean ignoreAllAlphabetGroups) {
        ArrayList<String> resultArray = new ArrayList<String>();

        group = removeSpecialCharacters(group);

        if (group.length() <= charactersInGroup && group.length() > 0) {
            // Replace Os, Bs, Hs, etc
            group = replaceAlphabets(group);

            // Check if all characters in group are alphabets
            if (isAllAlphabets(group)) {
                if (!ignoreAllAlphabetGroups) {
                    // Return empty if we should consider all alphabet groups
                    resultArray.add("");
                }
            } else if (isAllNumbers(group)) {
                if (group.length() != charactersInGroup) {
                    // Return empty
                    resultArray.add("");
                } else {
                    resultArray.add(group);
                }
            } else {
                //Return null
            }
        } else if (group.length() > charactersInGroup) {
            //Remove Alphabets
            group = removeAlphabets(group);

            if (isAllNumbers(group)) {
                if (group.length() > charactersInGroup) {
                    // Return empty
                    resultArray.add(group.substring(group.length() - charactersInGroup, group.length()));
                } else if (group.length() < charactersInGroup) {
                    resultArray.add("");
                } else {
                    resultArray.add(group);
                }
            }
        } else {
            //Return nil, empty group
        }

        return resultArray;
    }

    private String removeAlphabets(String group) {
        return group.replaceAll("[^0-9]", "");
    }

    private boolean isAllNumbers(String group) {
        return group.matches("[0-9]+");
    }

    private boolean isAllAlphabets(String group) {
        return group.matches("[a-zA-Z]+");
    }

    private String replaceAlphabets(String group) {

        Iterator<Map.Entry<String, String>> it = characterReplacement.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            while ((group != null) && (group.indexOf(pairs.getKey()) >= 0)) {
                group = group.replace(pairs.getKey(), pairs.getValue());
            }

            //it.remove(); // avoids a ConcurrentModificationException
        }

        return group;
    }

    private ArrayList<String> splitGroups(List<String> groups) {
        ArrayList<String> resultGroups = new ArrayList<String>();
        for (String group : groups) {
            resultGroups.addAll(splitGroup(group));
        }

        return resultGroups;
    }

    private ArrayList<String> splitGroup(String group) {
        ArrayList<String> resultGroups = new ArrayList<String>();
        if (group.length() > charactersInGroup) {

            //Group does not have the proper character count to be split uniformly, try removing special characters and try splitting once again.
            group = removeSpecialCharacters(group);
            if ((group.length() % charactersInGroup) == 0) {
                //Can be split into groups with proper character count
                for (int i = 0; i < group.length(); i += charactersInGroup) {
                    resultGroups.add(group.substring(i, i + charactersInGroup));
                }
            } else {
                //Can't be split into  proper groups
                resultGroups.add(group);
            }

        } else if (group.length() > 0) {
            //Can't be split into  proper groups
            resultGroups.add(group);
        } else {
            //Ignore empty group
        }
        return resultGroups;
    }

    private String removeSpecialCharacters(String group) {
        return group.replaceAll("[^a-zA-Z0-9]", "");
    }

    private ArrayList<String> removeEmptyGroups(List<String> groups) {
        ArrayList<String> resultGroups = new ArrayList<String>();
        for (String group : groups) {
            if (group.length() > 0) {
                resultGroups.add(group);
            }
        }
        return resultGroups;
    }

}
